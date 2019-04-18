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

  // The idea is to take in the last state, and transition to the next from from there
  def tick(previousState: Option[State] = None): State = {
    // TODO: Fix particles vars
    // TODO Minimize the if else statements

    algorithmEnum match {
      case ParticleSwarmOptimization =>
        // Prefer pattern matching to if-else branching
        previousState match {
          case None =>
            // No previous state => initialize first state
            val (ps, bestSchedule, bestMakeSpan) = pso.initializePopulation()
            State(particles = ps,
                  generation = 1,
                  bestSchedule = bestSchedule,
                  bestMakeSpan = bestMakeSpan)

          case Some(prev) =>
            val (particles,
                 bestSchedule: Seq[OperationTimeSlot],
                 bestMakeSpan: Int) = pso.tick(prev.particles)
            State(particles, prev.generation + 1, bestSchedule, bestMakeSpan)
        }

      case BeesAlgorithm =>
        // Do same with bees, but also consider handling the "transition to next state" within the 'ba' and 'pso' or at least in separate functions
        ???
    }
  }
}
