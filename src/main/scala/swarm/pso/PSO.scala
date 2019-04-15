package swarm.pso

import jssp.{Job, Machine, OperationTimeSlot}
import swarm.SwarmUtils

import scala.math.max
import scala.util.Random

/**
  * Particle Swarm Optimization
  * Based on Investigation of Particle Swarm Optimization for Job Shop Scheduling Problem
  * by Zhixiong Liu
  */
case class PSO(jobs: Seq[Job], machines: Seq[Machine]) {

  /**
    * Acceleration constants (c1 + c2 = 4)
    * Low values allow particles to roam far from target regions before being tugged back
    * High values result in abrupt movement towards, or past, target regions
    */
  val personalAccelerationConstant: Double = 2.0 // c1
  val globalAccelerationConstant: Double = 2.0 // c2

  val populationSize: Int = 50 // [10-50]


  /**
    * Large inertia weight facilitates searching new area (0.9)
    * Small inertia weight facilitates fine-searching in the current search area (0.4)
    */
  var inertiaWeight: Double = 0.9
  val inertiaWeightDecay: Double = 0.995
  val minInertiaWeight = 0.4

  var globalBestParticle: Particle = _

  def initializePopulation(): (Seq[Particle], Seq[OperationTimeSlot], Int) = {
    val particles: Seq[Particle] = for (_ <- 0 to populationSize) yield {
      val positionAndVelocity: Seq[(Int, Double, Double)] = SwarmUtils.initializePositionAndVelocity(jobs)
      val orderedPositionAndVelocity: Seq[(Int, Double, Double)] = positionAndVelocity.sortBy(_._2)
      val orderedPosition = orderedPositionAndVelocity.map(_._1)
      val schedule = SwarmUtils.decodePositionToSchedule(jobs, machines, orderedPosition)
      val makeSpan = SwarmUtils.calculateMakeSpan(schedule)
      val localBestPosition = positionAndVelocity.map(_._2)
      val particle = Particle(positionAndVelocity, makeSpan, schedule, localBestPosition, makeSpan, schedule)

      if (globalBestParticle == null || particle.makeSpan < globalBestParticle.makeSpan) {
        globalBestParticle = particle
      }

      particle
    }

    (particles, globalBestParticle.localBestSchedule, globalBestParticle.localBestMakeSpan)
  }

  def tick(particles: Seq[Particle]): (Seq[Particle], Seq[OperationTimeSlot], Int) = {
    val newParticles: Seq[Particle] = for (particle: Particle <- particles) yield {
      val newPositionAndVelocity: Seq[(Int, Double, Double)] = particle.calculateNewPositionAndVelocity(particle.positionAndVelocity,
                                                                                                        inertiaWeight,
                                                                                                        personalAccelerationConstant,
                                                                                                        globalAccelerationConstant,
                                                                                                        particle.localBestPosition,
                                                                                                        globalBestParticle.localBestPosition)
      val orderedNewPositionAndVelocity: Seq[(Int, Double, Double)] = newPositionAndVelocity.sortBy(_._2)
      val orderedNewPosition = orderedNewPositionAndVelocity.map(_._1)
      val newSchedule = SwarmUtils.decodePositionToSchedule(jobs, machines, orderedNewPosition)
      val newMakeSpan = SwarmUtils.calculateMakeSpan(newSchedule)

      val newPosition = newPositionAndVelocity.map(_._2)

      val newParticle = {
        if (newMakeSpan < particle.localBestMakeSpan) particle.copy(newPositionAndVelocity, newMakeSpan, newSchedule, newPosition, newMakeSpan, newSchedule)
        else particle.copy(newPositionAndVelocity, newMakeSpan, newSchedule, particle.localBestPosition, particle.localBestMakeSpan, particle.localBestSchedule)
      }

      if (newParticle.makeSpan < globalBestParticle.makeSpan) {
        globalBestParticle = newParticle
      }

      newParticle
    }

    inertiaWeight = inertiaWeight * inertiaWeightDecay
    inertiaWeight = if (inertiaWeight < minInertiaWeight) minInertiaWeight else inertiaWeight

    (newParticles, globalBestParticle.localBestSchedule, globalBestParticle.localBestMakeSpan)
  }
}
