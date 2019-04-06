package swarm.pso

import scala.util.Random

/**
  * Representation: Operation-Particle Position Sequence (OPPS)
  * The mapping between the particle and the scheduling solution is established
  * through connecting the operation sequence of all the jobs with the particle position sequence
  */
case class Particle(positionAndVelocity: Seq[(Int, Double, Double)],
                    makeSpan: Int,
                    localBestPosition: Seq[Double],
                    localBestMakeSpan: Int) {

  def calculateNewPositionAndVelocity(positionAndVelocity: Seq[(Int, Double, Double)],
                                      inertiaWeight: Double,
                                      personalAccelerationConstant: Double,
                                      globalAccelerationConstant: Double,
                                      localBestPosition: Seq[Double],
                                      globalBestPosition: Seq[Double]): Seq[(Int, Double, Double)] = {
    val jobIds = positionAndVelocity.map(_._1)
    val position = positionAndVelocity.map(_._2)
    val velocity = positionAndVelocity.map(_._3)

    val newVelocity: Seq[Double] = calculateNewVelocity(position,
                                                        velocity,
                                                        inertiaWeight,
                                                        personalAccelerationConstant,
                                                        globalAccelerationConstant,
                                                        localBestPosition,
                                                        globalBestPosition)

    val newPosition: Seq[Double] = calculateNewPosition(position,
                                                        newVelocity)


    // TODO: Find a better way to merge Seq(a), Seq(b), Seq(c) to Seq(a, b, c)
    val newPositionAndVelocity = for ((jobId, i) <- jobIds.zipWithIndex) yield {
      (jobId, newPosition(i), newVelocity(i))
    }

    newPositionAndVelocity
    //    (jobIds, newPosition, newVelocity)
  }

  def calculateNewPosition(position: Seq[Double], newVelocity: Seq[Double]): Seq[Double] = {
    // TODO: Find function to add each element with same index of two arrays into a new array
    val newPosition = for ((operationWeight, i) <- position.zipWithIndex) yield {
      operationWeight + newVelocity(i)
    }

    newPosition
  }

  def calculateNewVelocity(position: Seq[Double],
                           velocity: Seq[Double],
                           inertiaWeight: Double,
                           personalAccelerationConstant: Double,
                           globalAccelerationConstant: Double,
                           personalBestPosition: Seq[Double],
                           globalBestPosition: Seq[Double]): Seq[Double] = {


    val random: Random = Random
    val randomPersonalBestWeight = random.nextDouble()
    val randomGlobalBestWeight = random.nextDouble()

    val newVelocity: Seq[Double] = for ((operationVelocity, i) <- velocity.zipWithIndex) yield {
      val newOperationVelocity = (operationVelocity + inertiaWeight) +
        (personalAccelerationConstant * randomPersonalBestWeight * (personalBestPosition(i) - position(i))) +
        (globalAccelerationConstant * randomGlobalBestWeight * (globalBestPosition(i) - position(i)))

      if (newOperationVelocity < Particle.minVelocity) Particle.minVelocity
      else if (newOperationVelocity > Particle.maxVelocity) Particle.maxVelocity
      else newOperationVelocity
    }

    newVelocity
  }
}

object Particle {
  // TODO: These are used in Particle and in JSSP, is this a good place to store them?
  val minVelocity: Double = -2.0
  val maxVelocity: Double = 2.0
}
