package swarm.pso

import jssp.OperationTimeSlot

import scala.util.Random

/**
  * Representation: Operation-Particle Position Sequence (OPPS)
  * The mapping between the particle and the scheduling solution is established
  * through connecting the operation sequence of all the jobs with the particle position sequence
  */
case class Particle(positionAndVelocity: Seq[(Int, Double, Double)],
                    makeSpan: Int,
                    schedule: Seq[OperationTimeSlot],
                    localBestPosition: Seq[Double],
                    localBestMakeSpan: Int,
                    localBestSchedule: Seq[OperationTimeSlot]) {

  def calculateNewPositionAndVelocity(positionAndVelocity: Seq[(Int, Double, Double)],
                                      inertiaWeight: Double,
                                      personalAccelerationConstant: Double,
                                      globalAccelerationConstant: Double,
                                      localBestPosition: Seq[Double],
                                      globalBestPosition: Seq[Double]): Seq[(Int, Double, Double)] = {

    // TODO: you could just do this
    val (jobIds, position, velocity): (Seq[Int], Seq[Double], Seq[Double]) = positionAndVelocity.unzip3


    val newVelocity: Seq[Double] = calculateNewVelocity(position,
                                                        velocity,
                                                        inertiaWeight,
                                                        personalAccelerationConstant,
                                                        globalAccelerationConstant,
                                                        localBestPosition,
                                                        globalBestPosition)

    val newPosition: Seq[Double] = calculateNewPosition(position,
                                                        newVelocity)


    // TODO: Find a better way to merge Seq(a), Seq(b), Seq(c) to Seq(a, b, c) -- like this?
    val newPositionAndVelocity = jobIds.indices.map(i => (jobIds(i), newPosition(i), newVelocity(i)))

    newPositionAndVelocity
  }

  def calculateNewPosition(position: Seq[Double], newVelocity: Seq[Double]): Seq[Double] = {
    // TODO: Find function to add each element with same index of two arrays into a new array -- like this?
    position.zip(newVelocity).map{case (p, v) => p + v }
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
  // TODO: These are used in Particle and in JSSP, is this a good place to store them, or should they be passed as parameter?
  val minVelocity: Double = -2.0
  val maxVelocity: Double = 2.0
}
