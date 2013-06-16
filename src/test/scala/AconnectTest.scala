package aconnect

import org.scalatest.FunSuite

class AconnectTest extends FunSuite {

  test("parse inputs") {
    expect(Seq(

      Client(0, "System", Seq(
        Port(0, "Timer"),
        Port(1, "Announce")
      )),

      Client(14, "Midi Through", Seq(
        Port(0, "Midi Through Port-0")
      )),

      Client(24, "KeyRig 49", Seq(
        Port(0, "KeyRig 49 MIDI 1")
      )),

      // I don't know whether names actually can contain single quotes, but I added
      // one with the assumption that single quotes are escaped by backslash.
      Client(127, "crazy testin' thing", Seq(
        Port(24, "lolwut")
      ))

    )) {
      parseString(read("aconnect-inputs.txt"))
    }

  }

  test("parse outputs") {
    expect(Seq(

      Client(14, "Midi Through", Seq(
        Port(0, "Midi Through Port-0")
      )),

      Client(128, "ZynAddSubFX", Seq(
        Port(0, "ZynAddSubFX")
      ))

    )) {
      parseString(read("aconnect-outputs.txt"))
    }
  }

  def read(filename: String): String =
    io.Source.fromURL(getClass.getClassLoader.getResource(filename)).mkString

}
