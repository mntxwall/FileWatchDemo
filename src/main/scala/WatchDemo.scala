import java.nio.file._

object WatchDemo {

  def main(args: Array[String]): Unit = {
    println("HelloWorld")
     val watchService:WatchService
      = FileSystems.getDefault.newWatchService


    val path = Paths.get("/home/cw")
    path.register(
      watchService,
      StandardWatchEventKinds.ENTRY_CREATE,
      StandardWatchEventKinds.ENTRY_DELETE,
      StandardWatchEventKinds.ENTRY_MODIFY)

    var key:WatchKey = watchService.take()

    while (key != null){
      key.pollEvents().forEach{ x =>
        println(s"Event kind: ${x.kind} . File affected: ${x.context}")
      }
      key = watchService.take()
    }




  }
}
