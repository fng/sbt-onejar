onejar-sbt
============

* Packages your project using [One-JAR™](http://one-jar.sourceforge.net)*

onejar-sbt is a [simple-build-tool](http://code.google.com/p/simple-build-tool/)
plugin for building a single executable JAR containing all your code and dependencies
as nested JARs.

Currently One-JAR version 0.9.7 is used. This is included with the plugin, and need not be separately downloaded.

Alpha Release
-------------

This is the first release of the plugin. Mind the sharp edges, use at your own risk, etc.

How To Use
----------

**First**, specify onejar-sbt as a dependency in
`project/plugins/Plugins.scala`:

    class Plugins(info: sbt.ProjectInfo) extends sbt.PluginDefinition(info) {
      val retronymSnapshotRepo = "retronym's repo" at "http://retronym.github.com/repo/releases"
      val onejarSBT = "com.github.retronym" % "sbt-onejar" % "0.2"
    }

**Second**, add the `OneJarProject` trait to your project:
    import com.github.retronym.OneJarProject

    class MyProject(info: ProjectInfo) extends DefaultProject(info) with OneJarProject {

      override def mainClass = Some("acme.FizzleBlaster")
    }

**Third**, run `onejar` task to generate, erm, one JAR. Run it with `java -jar <jarname>`.

What's in the JAR?
------------------

As an example of the structure, here is the contents of `scalaz-example_2.8.1-5.1-SNAPSHOT-onejar.jar`, a package built
from the Scalaz examples module.

    .
    |-- META-INF
    |           `-- MANIFEST.MF
    |-- OneJar.class
    |-- boot-manifest.mf
    |-- com
    |   `-- simontuffs
    |       `-- onejar
    |           |-- Boot$1.class
    |           |-- Boot$2.class
    |           |-- Boot$3.class
    |           |-- Boot.class
    |           |-- Handler$1.class
    |           |-- Handler.class
    |           |-- IProperties.class
    |           |-- JarClassLoader$1.class
    |           |-- JarClassLoader$2.class
    |           |-- JarClassLoader$ByteCode.class
    |           |-- JarClassLoader$FileURLFactory$1.class
    |           |-- JarClassLoader$FileURLFactory.class
    |           |-- JarClassLoader$IURLFactory.class
    |           |-- JarClassLoader$OneJarURLFactory.class
    |           |-- JarClassLoader.class
    |           |-- OneJarFile$1.class
    |           |-- OneJarFile$2.class
    |           |-- OneJarFile.class
    |           `-- OneJarURLConnection.class
    |-- doc
    |   `-- one-jar-license.txt
    |-- lib
    |   |-- scala-library.jar
    |   |-- scalaz-core_2.8.1-5.1-SNAPSHOT.jar
    |   |-- scalaz-example_2.8.1-5.1-SNAPSHOT.jar
    |   |-- scalaz-geo_2.8.1-5.1-SNAPSHOT.jar
    |   |-- scalaz-http_2.8.1-5.1-SNAPSHOT.jar
    |   `-- servlet-api-2.5.jar
    |-- main
        `-- scalaz-example_2.8.1-5.1-SNAPSHOT.jar

How does it compare to XXX?
---------------------------

There are other ways to package your application. I based this plugin on Coda Hale's
[sbt-assembly](https://github.com/codahale/assembly-sbt) which builds an über-JAR, directly containing
all classes and resources merged together. This approach must be used carefully
when there are resources with the same name across multiple JARs.

Kris Nuttycombe's [sbt-proguard-plugin](http:github.com/nuttycom/sbt-proguard-plugin) also creates an über-JAR, with the
possibility to obfuscate and shrink. This is popular for targeting Android. Proguard is a powerful tool, and it
takes some effort to configure it correctly.

SBT itself can create WAR files from WebProjects. With a little work, you could probably get a self-executing WAR file
using embedded Jetty or Winstone. This would make a good SBT plugin project for someone!

One-JAR employs some classloader magic to sidestep the über-JAR limitations. It does support
classpath scanning, as used in Spring and Hibernate, although there seems to be a performance penalty for this. But
if your application, or it's libraries, also relies on classloader trickery, you might run into some problems. Caveat Emptor.

Looking for something else? One of these might suit: WebStart, Exe4J, jsmooth, izpack.

http://stackoverflow.com/questions/1967549/java-packaging-tools-alternatives-for-jsmooth-launch4j-onejar

Bugs
----

Please report bugs and feature requests to the GitHub issue tracker. Bugs with small sample project will get the most
attention. Forks and Pull Requests are also welcome.

Problems with One-JAR itself should be reported on it's [Issue Tracker](http://sourceforge.net/tracker/?group_id=111153)

License
-------

Copyright (c) 2011-2011 Jason Zaugg

Published under The MIT License, see LICENSE

The One-JAR License is reproduced below. This plugin is not endorsed in anyway by One-JAR.

>One-JAR (http://one-jar.sourceforge.net).  Copyright (c) 2004,
>P. Simon Tuffs (simon@simontuffs.com).  	All rights reserved.
>
>Redistribution and use in source and binary forms, with or without
>modification, are permitted provided that the following conditions are met:
>
>Redistributions of source code must retain the above copyright notice, this
>list of conditions and the following disclaimer.
>
>Redistributions in binary form must reproduce the above copyright notice,
>this list of conditions and the following disclaimer in the documentation
>and/or other materials provided with the distribution.
>
>Neither the name of P. Simon Tuffs, nor the names of any contributors,
>nor the name One-JAR may be used to endorse or promote products derived
>from this software without specific prior written permission.
>
>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
>AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
>IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
>ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
>LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
>CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
>SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
>INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
>CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
>ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
>POSSIBILITY OF SUCH DAMAGE.