package com.github.retronym

import sbt._
import java.util.jar.Attributes.Name._
import java.util.jar.Attributes.Name
import java.lang.String
import collection.mutable.ListBuffer

trait OneJarProject extends DefaultProject {

  override def classpathFilter = super.classpathFilter -- "*-sources.jar" -- "*-javadoc.jar"

  def onejarOutputPath : Path      = defaultJarPath("-onejar.jar")
  def onejarTemporaryPath : Path   = outputPath / "onejar"
  def onejarClasspath : PathFinder = runClasspath
  def onejarExtraJars : PathFinder = mainDependencies.scalaJars
  val oneJarResourceName: String   = "one-jar-boot-0.97.jar"


  // define the path to the splash-screen image
  // e.g. override lazy val splashScreenImage: Option[Path] = Some(splashScreenImageInMainResources("efg-onejar-splashscreen.gif"))
  lazy val splashScreenImage: Option[Path] = None

  //Searches the mainResourcesPath for a file matching the passed imageName
  final def splashScreenImageInMainResources(imageName: String): Path = {
    val splashScreenFinder = mainResourcesPath ** imageName

    val splashImage = splashScreenFinder.get.toList match {
      case List(splashPath) => splashPath
      case _ => error("Not exactly one Path for Splash image found!")
    }
    splashImage
  }

  def onejarPackageOptions: Seq[PackageOption] = {
    val manifestAttributes: ListBuffer[(java.util.jar.Attributes.Name, String)] = new ListBuffer
    manifestAttributes + (MAIN_CLASS, "com.simontuffs.onejar.Boot")
    //add splash-screen image to the manifest
    splashScreenImage.foreach(imagePath => manifestAttributes + (new Name("SplashScreen-Image"), imagePath.name))
    List(ManifestAttributes(manifestAttributes: _*))
  }

  lazy val onejar = onejarTask(onejarTemporaryPath,
    onejarClasspath,
    onejarExtraJars
  ) dependsOn (`package`) describedAs ("Builds a single-file, executable JAR using One-JAR")

  def onejarTask(tempDir: Path, classpath: PathFinder, extraJars: PathFinder) =
    packageTask(Path.lazyPathFinder(onejarPaths(tempDir, classpath, extraJars)), onejarOutputPath, onejarPackageOptions)

  def onejarPaths(tempDir: Path, classpath: PathFinder, extraJars: PathFinder) = {
    import xsbt.FileUtilities._
    FileUtilities.clean(tempDir, log)

    val (libs, directories) = classpath.get.toList.partition(ClasspathUtilities.isArchive)

    // Unpack One-Jar itself, which is a classpath resource of this Plugin
    val oneJarResourceStream = {
      val s = this.getClass.getClassLoader.getResourceAsStream(oneJarResourceName)
      if (s == null) error("could not load: " + oneJarResourceName)
      s
    }
    val notManifest: xsbt.NameFilter = -(new xsbt.ExactFilter("META-INF/MANIFEST.MF"))
    unzip(oneJarResourceStream, tempDir.asFile, notManifest)
    delete(tempDir / "src" asFile)

    val tempMainPath = tempDir / "main"
    val tempLibPath = tempDir / "lib"
    Seq(tempLibPath, tempMainPath).foreach(_.asFile.mkdirs)

    // Copy all dependencies to "lib"
    val otherProjectJars = topologicalSort.flatMap{
      case x: BasicPackagePaths => List(x.jarPath)
      case _ => Nil
    }
    val libPaths: List[Path] = libs ++ extraJars.get ++ otherProjectJars

    getOrThrow(FileUtilities.copyFlat(List(jarPath), tempMainPath, log))
    getOrThrow(FileUtilities.copyFlat(libPaths, tempLibPath, log))

    //Copy splash-screen image
    splashScreenImage.foreach(imagePath => {
      FileUtilities.copyFile(imagePath, tempDir / imagePath.name, log) match {
        case Some(e) => error(e)
        case None => {}
      }
    })

    // Return the paths that will be added to the -onejar.jar
    descendents(tempDir ##, "*").get
  }

  def getOrThrow[X](result: Either[String, X]): X =
    result match {
      case Left(s) => error(s)
      case Right(x) => x
    }
}
