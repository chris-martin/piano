package piano

import scala.sys.process._

package object filesystem {

  type File = java.io.File

  def findFiles(directory: File, query: String): Seq[File] =

    Seq(
      "find", directory.getPath,
      "-type", "f",
      "-iname", "*%s*" format query
    )
      .lines
      .filter(_.nonEmpty)
      .map(new File(_))

}
