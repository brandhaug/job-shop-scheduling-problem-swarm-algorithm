package jssp

import swarm.pso.{PSO, Particle}

/**
  * Job Shop Scheduling Problem
  */
class JSSP(jobs: Seq[Job], machines: Seq[Machine]) {
  var generation: Int = 0
  var particles: Seq[Particle] = _
  var bestSchedule: Seq[OperationTimeSlot] = _
  var bestMakeSpan: Int = _
  val pso: PSO = PSO(jobs, machines)

  def tick(): (Int, Seq[OperationTimeSlot], Int) = {
    //    TODO: Make commented code work
    //    (particles, bestSchedule, bestMakeSpan) = if (generation == 0) pso.initializePopulation() else pso.tick(particles)
    val (par, bs, bms) = if (generation == 0) pso.initializePopulation() else pso.tick(particles)
    particles = par
    bestSchedule = bs
    bestMakeSpan = bms

    generation += 1
    (generation, bestSchedule, bestMakeSpan)
  }
}