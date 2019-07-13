import java.io.{BufferedInputStream, BufferedOutputStream, FileInputStream, FileOutputStream}
import java.net.URI
import java.nio.file._
import java.util.zip.{ZipEntry, ZipInputStream}

import scala.sys.process.ProcessBuilder.Source

object WatchDemo {

  def main(args: Array[String]): Unit = {
    println("HelloWorld")

    //unzip(Paths.get("/home/cw/Work/hi.zip"), Paths.get("/home/cw/unzipFiles/"))
    //unzipFiles()
    watch()

    //zipFile(Paths.get("/home/cw/zipfiles/hi.txt"), Paths.get("/home/cw/Work/hi.zip"))
    //zipFile2(Paths.get("/home/cw/zipFiles/hi.txt"), "/home/cw/unzipFiles/hi.zip")


   // Files.copy(Paths.get("/home/cw/zipFiles/hi.txt"), Paths.get("/home/cw/unzipFiles/b.txt"),StandardCopyOption.REPLACE_EXISTING)
  }

  def watch() = {

    val watchService:WatchService
    = FileSystems.getDefault.newWatchService


    val path = Paths.get("/home/cw/Work")
    val unzipPath = Paths.get("/home/cw/unzipFiles")
    path.register(
      watchService,
      StandardWatchEventKinds.ENTRY_CREATE)

    var key:WatchKey = watchService.take()

    while (key != null){
      key.pollEvents().forEach{ x =>

        println(s"Event kind: ${x.kind} . File affected: ${x.context}")

        Thread.sleep(5000)
        unzipFiles(unzipPath, path.resolve(x.context.toString))

        println("unzip End")

      }
      key.reset()
      key = watchService.take()
    }
  }

  def unzipFiles(outDir: Path, source: Path) = {

    val buffer = new Array[Byte](1024)
    val stream = new ZipInputStream(new BufferedInputStream(new FileInputStream(source.toFile)))
    val entry = stream.getNextEntry
    doReadZipFile(entry, outDir, buffer, stream)

    stream.close()
  }

  def doReadZipFile(entry: ZipEntry, outDir: Path, buffer: Array[Byte], stream: ZipInputStream):Int = {
    entry match {
      case s:ZipEntry => {
        println("Unzipping: " + s.getName)
        val filePath = outDir.resolve(s.getName)
        val bos = new BufferedOutputStream(new FileOutputStream(filePath.toFile), buffer.length)
        val size: Int = stream.read(buffer)
        doWriteUnzipFiles(size, bos, buffer, stream)
        bos.flush()
        doReadZipFile(stream.getNextEntry, outDir, buffer, stream)
        /*
        *  close every stream when call ends
        * */
        bos.close()
        1
      }
      case _ => 0
    }

  }
  def doWriteUnzipFiles(size: Int, bos: BufferedOutputStream, buffer: Array[Byte], stream: ZipInputStream):Int = {

    size match {
      case x if x != -1 => {
        bos.write(buffer, 0, x)
        doWriteUnzipFiles(stream.read(buffer), bos, buffer, stream)
      }
      case _ => 0
    }
    /*
    if(size != -1){
      bos.write(buffer, 0, size)
      doWriteUnzipFiles(stream.read(buffer), bos, buffer, stream)
    }
    else 0*/

  }

  def zipFile2(source: Path, dest: String) = {

    if (!Files.exists(Paths.get(dest))){

      val  env = new java.util.HashMap[String, String]()
      env.put("create", "true")
      val uri = URI.create(s"jar:file:${dest}")
      val zipFileSystem = FileSystems.newFileSystem(uri, env)

      val top = zipFileSystem.getPath("/hi.txt")

      Files.copy(source, top,StandardCopyOption.REPLACE_EXISTING)
      //iles.walk(zipFileSystem.getPath("/"))

      /*
      Files.walk(source).forEach{file =>

      //  println(top.relativize(file).toString)

        //zipFileSystem.getPath()
        val tmp = top.getFileSystem.getPath("/hi.txt")
        Files.createFile(tmp)

        Files.copy(file, tmp,StandardCopyOption.REPLACE_EXISTING)

        //dest.resolve()

        //val target = dest.resolve(top.relativize(file).toString)
        ///System.out.println("Extracting " + target)
        //Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING)

      }*/

      /*Files.walk(source).forEach{file =>
        val target = zipFileSystem.getPath("/a.txt")
        //val target = root.resolve(source.relativize(file).toString)
        Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING)
        //println(target.getFileName)
      }*/
      /*
            val target = zipFileSystem.getPath("/a.txt")
            //val target = root.resolve(source.relativize(file).toString)
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)*/
      //println(dest.toUri.getPath)

      //Files.createFile(dest)
    }
    val zipFileSystem = FileSystems.newFileSystem(Paths.get(dest), null)
    val target = zipFileSystem.getPath("/hi.txt")
    //val target = root.resolve(source.relativize(file).toString)
   Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
    /*
    Files.walk(source).forEach{file =>
      val target = zipFileSystem.getPath("/a.txt")
      //val target = root.resolve(source.relativize(file).toString)
      Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING)
      //println(target.getFileName)
    }
    */

    /*val zipFileSystem = FileSystems.newFileSystem(dest, null)
    val root = zipFileSystem.getPath("/")
    val top = zipFileSystem.getPath(root.toString, source.toString)
    val parent = top.getParent

    if (Files.notExists(parent)) {
      println("AAAAA")
      Files.createDirectories(parent)
    }
    Files.copy(source,
      top,
      StandardCopyOption.REPLACE_EXISTING)
      */
  }

  def zipFile(source: Path, dest: Path) = {

    if (!Files.exists(dest)){

      val  env = new java.util.HashMap[String, String]()
      env.put("create", "true")
      val uri = URI.create(s"jar:file:${dest.toUri.getPath}")
      FileSystems.newFileSystem(uri, env)
      //val root = zipFileSystem.getPath("/")

      /*Files.walk(source).forEach{file =>
        val target = zipFileSystem.getPath("/a.txt")
        //val target = root.resolve(source.relativize(file).toString)
        Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING)
        //println(target.getFileName)
      }*/
/*
      val target = zipFileSystem.getPath("/a.txt")
      //val target = root.resolve(source.relativize(file).toString)
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)*/
      //println(dest.toUri.getPath)

      //Files.createFile(dest)
    }
    val zipFileSystem = FileSystems.newFileSystem(dest, null)
    val target = zipFileSystem.getPath("/hi.txt")
    //val target = root.resolve(source.relativize(file).toString)
    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
    /*
    Files.walk(source).forEach{file =>
      val target = zipFileSystem.getPath("/a.txt")
      //val target = root.resolve(source.relativize(file).toString)
      Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING)
      //println(target.getFileName)
    }
    */

    /*val zipFileSystem = FileSystems.newFileSystem(dest, null)
    val root = zipFileSystem.getPath("/")
    val top = zipFileSystem.getPath(root.toString, source.toString)
    val parent = top.getParent

    if (Files.notExists(parent)) {
      println("AAAAA")
      Files.createDirectories(parent)
    }
    Files.copy(source,
      top,
      StandardCopyOption.REPLACE_EXISTING)
      */
  }

  def unzip(source: Path, dest: Path) = {

    //val zipInputStream = new ZipInputStream(Files.newInputStream(source))

    val zipFileSystem = FileSystems.newFileSystem(source, null)

    val top = zipFileSystem.getPath("/")
    //iles.walk(zipFileSystem.getPath("/"))

    Files.walk(top).skip(1).forEach{file =>
      val target = dest.resolve(top.relativize(file).toString)
      System.out.println("Extracting " + target)
      Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING)

    }
  }

  def unzipJob(zipStream: ZipInputStream, toPath: Path) = {

    val tmp = zipStream.getNextEntry
    if (tmp != null){
      Files.copy(zipStream, toPath)
      //unzipJob(zipStream, toPath)
    }

  }

  def unzipCommon() = {


  }
}