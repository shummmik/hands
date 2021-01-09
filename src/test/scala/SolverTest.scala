import  org.scalatest._
import  Solver._

class SolverTest extends FunSuite{
  val inputLines: List[String]  = read("./input.txt")
  val outputLines: List[String] = read("./output.txt")
  for(x<-inputLines.indices){
    test("Test row: " + x){
      assert(process(inputLines(x)) == outputLines(x))
    }
  }
}
