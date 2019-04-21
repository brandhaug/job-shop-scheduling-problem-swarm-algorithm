package jssp

import jssp.AlgorithmEnum._
import swarm.pso.{PSO, Particle}

/**
  * Job Shop Scheduling Problem
  */
case class State(particles: Seq[Particle],
                 generation: Int = 0,
                 bestSchedule: Seq[OperationTimeSlot],
                 bestMakeSpan: Int)
class JSSP(jobs: Seq[Job],
           machines: Seq[Machine],
           algorithmEnum: AlgorithmEnum) {

  val pso: PSO = PSO(jobs, machines)

  def tick(previousState: Option[State] = None): State = {
    algorithmEnum match {
      case ParticleSwarmOptimization =>
        previousState match {
          case None =>
            // No previous state => initialize first state
            val (particles, bestSchedule, bestMakeSpan) = pso.initializePopulation()
            State(particles = particles,
                  generation = 1,
                  bestSchedule = bestSchedule,
                  bestMakeSpan = bestMakeSpan)

          case Some(prevState) =>
            val (particles,
                 bestSchedule: Seq[OperationTimeSlot],
                 bestMakeSpan: Int) = pso.tick(prevState.particles)
            State(particles, prevState.generation + 1, bestSchedule, bestMakeSpan)
        }

      case BeesAlgorithm =>
        // Do same with bees, but also consider handling the "transition to next state" within the 'ba' and 'pso' or at least in separate functions
        ???
    }
  }
}
