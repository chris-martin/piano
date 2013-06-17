package piano

package object shutdown {

  def addShutdownHook(f: => Unit) {
    Runtime.getRuntime.addShutdownHook(new Thread { override def run() { f } })
  }

}
