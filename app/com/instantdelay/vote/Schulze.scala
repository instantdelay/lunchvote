package com.instantdelay.vote

class Schulze[T] extends RankedMethod[T] {
  override def apply(candidates: Set[T], ballots: Seq[RankBallot[T]]): Seq[T] = {
    val size = candidates.size
    val candidateIndices = candidates.zipWithIndex.toMap
    val mat = getPrefCountMatrix(ballots, candidateIndices)
    val p = Array.ofDim[Int](size, size)

    for (i <- 0 until size; j <- 0 until size) {
      if (mat(i)(j) > mat(j)(i)) {
        p(i)(j) = mat(i)(j)
      }
    }

    for (k <- 0 until size; i <- 0 until size; j <- 0 until size) {
      p(i)(j) = Math.max(p(i)(j), Math.min(p(i)(k), p(k)(j)))
    }

    candidates.toList.sortWith((a, b) => {
      val aIdx = candidateIndices(a)
      val bIdx = candidateIndices(b)
      p(bIdx)(aIdx) < p(aIdx)(bIdx)
    })
  }

  private def getPrefCountMatrix(ballots:Seq[RankBallot[T]], indices:Map[T, Int]) = {
    val size = indices.size
    val mat = Array.ofDim[Int](size, size)
    for (ballot <- ballots) {
      ballot.prefs.toList.combinations(2).foreach {
        case List(a, b) =>
          if (a.rank < b.rank) {
            mat(indices(a.candidate))(indices(b.candidate)) += 1
          }
          else {
            mat(indices(b.candidate))(indices(a.candidate)) += 1
          }
      }
    }
    mat
  }
}
