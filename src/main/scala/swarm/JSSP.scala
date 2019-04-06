package swarm

import main.{Job, Machine}
import swarm.pso.{PSO, Particle}

/**
  * Job Shop Scheduling Problem
  */
class JSSP(jobs: Seq[Job], machines: Seq[Machine]) {
  val PSO_ENUM = 0
  val BA_ENUM = 1
  val algorithmId: Int = PSO_ENUM
  var generation: Int = 0

  if (algorithmId == PSO_ENUM) {
    val pso: PSO = PSO(jobs, machines)

    var particles: Seq[Particle] = pso.initializePopulation()
    generation += 1

    while (true) {
      particles = pso.tick(particles)
    }
  } else if (algorithmId == BA_ENUM) {
  } else {
    println("Unknown algorithmId")
  }
}
