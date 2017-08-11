package com.instantdelay.vote

import org.scalatest.{FlatSpec, Matchers}

class SchulzeTest extends FlatSpec with Matchers {

  val schulze = new Schulze[String]
  val A = "A"
  val B = "B"
  val C = "C"
  val D = "D"
  val E = "E"

  private def vote(order:String) = {
    RankBallot(order.zipWithIndex.map {case (c, idx) => Pref(c.toString, idx)}.toSet)
  }

  implicit class MultiBallot[T](b:RankBallot[T]) {
    def ++(other:RankBallot[T]) = {
      Seq(b, other)
    }
    def ++(other:Seq[RankBallot[T]]) = {
      Seq(b) ++ other
    }
    def times(ct:Int) = {
      (1 to ct).map(_ => b)
    }
  }


  "schulze" should "handle a simple ordered pair" in {
    schulze(Set(A, B), Seq(vote("AB"))) should be (Seq(A, B))
  }

  it should "handle two identical ballots" in {
    schulze(Set(A, B), vote("AB") ++ vote("AB")) should be (Seq(A, B))
  }

  it should "handle the wikipedia example" in {
    schulze(Set(A, B, C, D, E),
      (vote("ACBED") times 5)
      ++ (vote("ADECB") times 5)
      ++ (vote("BEDAC") times 8)
      ++ (vote("CABED") times 3)
      ++ (vote("CAEBD") times 7)
      ++ (vote("CBADE") times 2)
      ++ (vote("DCEBA") times 7)
      ++ (vote("EBADC") times 8)) should be (Seq(E, A, C, B, D))
  }
}
