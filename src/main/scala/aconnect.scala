package piano

import scala.sys.process._
import scala.util.Try

package aconnect {

  case class Clients(clients: Seq[Client]) {

    def addresses: Seq[Address] =
      for (client <- clients; port <- client.ports)
        yield Address(client, port)

    def filterAddressesByName(query: String): Seq[Address] =
      filterAddressesByName(_.toLowerCase.contains(query.toLowerCase))

    def filterAddressesByName(predicate: String => Boolean): Seq[Address] =
      filterAddressesByPort(port => predicate(port.name)) ++
        filterAddressesByClient(client => predicate(client.name))

    def filterAddressesByPort(predicate: Port => Boolean): Seq[Address] =
      addresses.filter(a => predicate(a.port))

    def filterAddressesByClient(predicate: Client => Boolean): Seq[Address] =
      clients.filter(predicate).flatMap(_.addresses)

  }

  case class Client(id: Int, name: String, ports: Seq[Port]) {

    def addresses: Seq[Address] =
      for (port <- ports)
        yield Address(this, port)

  }

  case class Port(id: Int, name: String)

  case class Address(client: Client, port: Port) {

    def id: String = "%s:%s".format(client.id, port.id)

  }

}

package object aconnect {

  def connect(in: Address, out: Address, log: ProcessLogger): Try[Unit] =
    Try { Seq("aconnect", in.id, out.id).!!(log) }

  def inputs(log: ProcessLogger): Try[Clients] =
    Try { parseString(Seq("aconnect", "-i").!!(log)) }

  def outputs(log: ProcessLogger): Try[Clients] =
    Try { parseString(Seq("aconnect", "-o").!!(log)) }

  def parseString(s: String): Clients = {
    val result = Parser.parseAll(Parser.clients, s)
    result match {
      case Parser.NoSuccess(error, next) =>
        throw new RuntimeException(error + "\n" + next.pos)
      case _ => Clients(result.get)
    }

  }

  private object Parser extends scala.util.parsing.combinator.RegexParsers {

    override def skipWhitespace = false

    lazy val clients: Parser[Seq[Client]] = rep(client <~ """\n*""".r)

    lazy val client: Parser[Client] =
      "client" ~ whiteSpace ~> id ~ (":" ~ whiteSpace ~> name <~ """[^\n]*\n""".r) ~ ports ^^
        { case id ~ name ~ ports => Client(id, name, ports) }

    lazy val ports: Parser[Seq[Port]] = rep(port)

    lazy val port: Parser[Port] =
      whiteSpace ~> id ~ (whiteSpace ~> name) <~ "\n" ^^
        { case id ~ name => Port(id, name) }

    lazy val id: Parser[Int] = """(\d+)""".r ^^ { _.toInt }

    lazy val name: Parser[String] =
      "'" ~> """((?:\\'|[^'])+)""".r <~ "'" ^^
        { _.trim.replace("""\'""", "'") }

  }

}
