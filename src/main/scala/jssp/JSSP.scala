package jssp

import swarm.pso.{PSO, Particle}

/**
  * Job Shop Scheduling Problem
  */
class JSSP(jobs: Seq[Job], machines: Seq[Machine]) {
  var generation: Int = 0
  var particles: Seq[Particle] = _
  val pso: PSO = PSO(jobs, machines)

  def tick(): (Int, Seq[OperationTimeSlot], Int) = {
    // TODO: Fix particles vars
    val (_particles, bestSchedule: Seq[OperationTimeSlot], bestMakeSpan: Int) = {
      if (generation == 0) pso.initializePopulation()
      else pso.tick(particles)
    }
    particles = _particles

    generation += 1
    (generation, bestSchedule, bestMakeSpan)
  }
}