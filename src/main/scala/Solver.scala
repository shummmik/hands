import java.io._
import scala.io.Source
import scala.math.Ordering.Implicits.seqOrdering
import scala.util.matching.Regex

object Solver {

  val RANKS = "23456789tjqka"
  val PAIR_REGEX: Regex = s"""([$RANKS][cdhs])""".r
  val ERROR_PREFIX = "Error: "

  def read(file: String): List[String] ={
    val source= Source.fromFile(file)
    val listLines = source.getLines.toList
    source.close()
    listLines
  }


  def start(inputString: String):  Unit={
      try {
        val ListLines = read(inputString)
        for(i<-ListLines){
          println(process(i))
      } }catch {
        case e: FileNotFoundException => println(ERROR_PREFIX +"Couldn't find that file.")
        case e: IOException => println(ERROR_PREFIX +"Had an IOException trying to read that file")
        case e: NullPointerException => println(ERROR_PREFIX +"Null input string")
      }
  }

  //this algorithm i took from here
  //https://stackoverflow.com/questions/10363927/the-simplest-algorithm-for-poker-hand-evaluation
  //http://nsayer.blogspot.com/2007/07/algorithm-for-evaluating-poker-hands.html
  //it's pair histogram counts ranks and ranks
  def evaluate(hand: List[String]):  (List[Int], List[Int])  = {
    if (hand.length > 5){
      val scores = for (map <- hand.indices) yield Tuple2(evaluate(hand.slice(0, map):::hand.slice(1 + map, hand.length)), map)
      return scores.sorted.last._1
    }

    val recounts = (for(map <- hand) yield (RANKS.indexOf(map(0)), hand.mkString.count(_ == map(0)))).toMap.toList
    val counts = for(map <- recounts) yield (map._2, map._1)
    val (score, ranks) = counts.sorted.reverse.unzip

    //if all the cards are different, then the ranking changes slightly
   if (score.length == 5) {
     //token of rank straight
      val straight = (ranks.head - ranks.last ) == 4
     //token of rank flush
      val flush = (for (map <- hand) yield map(1)).distinct.length == 1

     Tuple2(if (flush) {if (straight) List(5) else List(3,1,1,2) } else  {if (straight)    List(3,1,1,1) else List(1) },
       if (ranks.slice(0,2) == (12,3)) List(3, 2, 1, 0, -1) else ranks)
   }
   else Tuple2(score, ranks)
  }

  //function for "omaha-holdem"
  def omaha_holdem(board_up: String, hands_up:List[String]): String={
    val board = board_up.toLowerCase
    val hands = hands_up.map(x => x.toLowerCase)
    if (check_set_maps(board, 5)) return  ERROR_PREFIX + "Invalid input board omaha-holdem"
    for (hand <- hands) if (check_set_maps(hand, 4)) return  ERROR_PREFIX + "Invalid input hand omaha-holdem"
    //In Omaha, you can only use two cards out of four. Let's create a set of possible pairs.
    val variants = for (i <- 0 until 4; j <- i until 4 if i != j) yield (i, j)
    val variants_hands = for (hand <- hands) yield
      (for (i <- variants) yield board + handtomaps(hand)(i._1) + handtomaps(hand)(i._2)).toList

    val rangeshands = (for(i<-hands.indices) yield (sorted_hands(variants_hands(i)).last._1, i)).toList
    val (indexs, compare) = get_indexs_compare(rangeshands.sorted)
    (for (i <- compare.indices) yield hands_up(indexs(i)) + compare(i)).mkString("") + hands_up(indexs.last)
  }

  //function for "texas-holdem"
  def holdem(board_up: String, hands_up:List[String]): String = {
    val board = board_up.toLowerCase
    val hands = hands_up.map(x => x.toLowerCase)
    if (check_set_maps(board, 5 )) return  ERROR_PREFIX + "Invalid input board texas-holdem"
    for (hand <- hands)  if (check_set_maps(hand, 2 ))return  ERROR_PREFIX + "Invalid input hand texas-holdem"
    val boardHands = (for(i<-hands.indices) yield board + hands(i)).toList
    val sortedhands = sorted_hands(boardHands)
    val (indexs, compare) = get_indexs_compare(sortedhands)
    (for (i <- compare.indices) yield hands_up(indexs(i)) + compare(i)).mkString("") + hands_up(indexs.last)
  }

  //function for splitting a set of cards into cards
  def handtomaps(hand: String ): List[String]={
    val maps = for (i <- 0 until (hand.length,2)) yield hand.slice(i,i+2)
    maps.toList
  }

  //function for "five-card-draw"
  def five_map(hands_up: List[String]): String = {
    val hands = hands_up.map(x => x.toLowerCase)
    for (hand <- hands)  if (check_set_maps(hand, 5)) return ERROR_PREFIX + "Invalid input hand five-card-draw"
    val sortedhands = sorted_hands(hands)
    val (indexs, compare) = get_indexs_compare(sortedhands)
    (for (i <- compare.indices) yield hands_up(indexs(i)) + compare(i)).mkString("") + hands_up(indexs.last)
  }

  //sort of equal rank
  def finish_sort(answer: String): String = {
    val answers = answer.split(" ")
    (for (i <- answers) yield i.split("=").sorted.mkString("=")).mkString(" ")
  }

  //returns the indices, and symbols of comparison
  def get_indexs_compare(sortedHands: List[((List[Int], List[Int]), Int)]): (List[Int], List[String]) = {
    val compare = for (i <- 0 until(sortedHands.length-1)) yield if (sortedHands(i)._1 == sortedHands(i+1)._1)  "=" else " "
    Tuple2(sortedHands.map(_._2), compare.toList)
  }
  //function for ranking and sorting
  def sorted_hands(hands: List[String]): List[((List[Int], List[Int]), Int)]= {
    val rankHands = for(hand <- hands.indices) yield Tuple2(evaluate(handtomaps(hands(hand))), hand)
    rankHands.toList.sorted
  }

  //checking for the validity of a set of cards
  def check_set_maps(maps: String, count: Int): Boolean = {
    if (PAIR_REGEX.findAllIn(maps).length == count) return false
    true
  }

  def process(line: String): String = {
    line.split("\\s+").toList match {
      case "texas-holdem" :: board :: hands   => finish_sort(holdem(board, hands))
      case "omaha-holdem" :: board :: hands   => finish_sort(omaha_holdem(board, hands))
      case "five-card-draw" :: hands          => finish_sort(five_map(hands))
      case x :: _                             => ERROR_PREFIX + "Unrecognized game type"
      case _                                  => ERROR_PREFIX + "Invalid input"
    }
  }
}