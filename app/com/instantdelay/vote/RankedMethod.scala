package com.instantdelay.vote

trait RankedMethod[T] extends ((Set[T], Seq[RankBallot[T]]) => Seq[T]) {

}
