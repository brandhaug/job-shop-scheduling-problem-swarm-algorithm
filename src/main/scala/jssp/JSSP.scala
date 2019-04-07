package jssp

import jssp.AlgorithmEnum._
import swarm.ba.{BA, Bee}
import swarm.pso.{PSO, Particle}

/**
  * Job Shop Scheduling Problem
  */
class JSSP(jobs: Seq[Job], machines: Seq[Machine], algorithmEnum: AlgorithmEnum) {
  var generation: Int = 0

  val pso: PSO = PSO(jobs, machines)
  var particles: Seq[Particle] = _

  val ba: BA = BA(jobs, machines)
  var bees: Seq[Bee] = _

  def tick(): (Int, Seq[OperationTimeSlot], Int) = {
    // TODO: Fix particles vars
    // TODO Minimize the if else statements
    if (algorithmEnum == ParticleSwarmOptimization) {
      val (_particles, bestSchedule: Seq[OperationTimeSlot], bestMakeSpan: Int) = {
        if (generation == 0) pso.initializePopulation()
        else pso.tick(particles)
      }
      particles = _particles

      generation += 1
      (generation, bestSchedule, bestMakeSpan)
    } else if (algorithmEnum == BeesAlgorithm) {
      val (_bees: Seq[Bee], bestSchedule: Seq[OperationTimeSlot], bestMakeSpan: Int) = {
        if (generation == 0) ba.initializePopulation()
        else ba.tick(bees)
      }
      bees = _bees

      generation += 1
      (generation, bestSchedule, bestMakeSpan)
    } else {
      throw new Error("Unknown algorithm")
    }
  }
}