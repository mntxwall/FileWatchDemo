import java.nio.file._
import java.util.zip.ZipInputStream

object WatchDemo {

  def main(args: Array[String]): Unit = {
    println("HelloWorld")
     val watchService:WatchService
      = FileSystems.getDefault.newWatchService

    val workPath: String = "/home/cw/Work"

    //path.get

    val path = Paths.get("/home/cw/Work")
    val path2 = Paths.get("/home/cw/UnZipFiles")
    path.register(
      watchService,
      StandardWatchEventKinds.ENTRY_CREATE)

    var key:WatchKey = watchService.take()

    while (key != null){
      key.pollEvents().forEach{ x =>
        println(s"Event kind: ${x.kind} . File affected: ${x.context}")


        val sourcePath = Paths.get(s"$workPath/${x.context.toString}")

        val zipInputStream = new ZipInputStream(Files.newInputStream(sourcePath))
        var entry = zipInputStream.getNextEntry

        while (entry != null){

          val toPath = path2.resolve(entry.getName)



          if (entry.isDirectory) {
            Files.createDirectory(toPath)
          }
          else {
            Files.createFile(toPath)
            Files.copy(zipInputStream, toPath)
          }

          entry = zipInputStream.getNextEntry
        }

        zipInputStream.close()



        //if (x.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) println("Hellow")
      }
      key.reset()
      key = watchService.take()
    }




  }
}
