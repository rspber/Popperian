package org.jpss.cai.libs.ubup3;

/*
	uses UABFUN, UCashST2, UAB, SysUtils, Classes
*/

//Universal Byte Prediction System V3 by Joao Paulo Schwarz Schuler
//
//These classes implement combinatorial NEURAL NETWORKS:
//* They are problem independent (universal).
//* After learning, these classes can predict/classify future states.
//* In this code, "neurons" are sometimes named as "relations".
//* Some neurons are a "test" that relates a condition (current state) to an OPERATION.
//* Some neurons are "operations" that transform the current state into the next state (SECOND NEURAL NETWORK LAYER).
//* Does a NEURON fire at a given state? Yes. Each neuron "tests" for the existence of a given state.
//* There are 2 neural network layers: tests and operations.
//* The current state and the predicted state are Arrays of Bytes. This is why you can find "AB" along the source code.
//* NEURONS in this unit resemble microprocessor OPERATIONS and TESTS.
//* Neurons in this unit ARE NOT floating point operations.
//* Neurons that correctly predict future states get stronger.
//* Stronger neurons win the prediction.
//
//If you aren't sure what/where to look at, please have a look at EasyLearnAndPredict
//

public class UBUP3
{
}
