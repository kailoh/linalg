
package backend.main;

import java.util.ArrayList;
import java.util.List;

import backend.blocks.Bracket;
import backend.blocks.Countable;
import backend.blocks.Matrix;
import backend.blocks.Numerical;
import backend.blocks.Op;
import backend.blocks.Operation;
import backend.operations.MM_PlusMinus;
import backend.operations.Solution;

/** Processes a sequence of Numericals representing a computation and generates a tree structure of solutions
 *  that allows the caller to understand how the computation was computed
 *  
 * @author baebi
 */
public class Parser {
	
	//Don't construct
	private Parser(){}
	
	/** Returns a tree of ParseNodes that each contain Solution objects.
	 *  The tree reflects the order that each solution was arrived at. Solutions
	 *  nearer to the leaves were computed first because of order of operations.
	 *  The root node is the last computation performed in the sequence
	 *  (ex: 4 * 2 + 3 * 4 -> '+' would be the last computation) The tree would look like:
	 *  
	 *           +
	 *         /   \
	 *        *     *
	 *       / \   / \
	 *      4   2  3  4
	 * 
	 * It's important to note that the ParseNodes each contain solution objects, so the leaves aren't
	 * actually present in the tree. Each solution contains its own inputs though, so all the information
	 * is there 
	 * 
	 * @param input A list of numericals representing a sequence of computations (ex: MatrixA * ScalarB + .... ) 
	 * @return A ParseNode root of a ParseNode tree
	 * 
	 */
	public static ParseNode parse(List<Numerical> input) throws IllegalArgumentException {
		checkValidInput(input);
		Numerical operationTree = createSortedTree(input); 
		return compute(operationTree);
	}
	
	
	/** Computes a sequence of computations organized into a tree structure of ParseNodes. (See
	 *  createSortedTree). EXPECTS ONLY VALID EXPRESSIONS AT THE BLOCK LEVEL (ie matrices can still have wrong
	 *  dimensions or have null entries, etc. But if for instance, the arguments to a matrix multiply will 
	 *  be matrices). 
	 * 
	 * @param root
	 * @return
	 */
	protected static ParseNode compute(Numerical root) throws IllegalArgumentException{

		if (root instanceof Operation){
			Operation rootAsOp = (Operation) root;
			Op op = rootAsOp.getType();
			switch (op){
				// addition
				case PLUS: {
					return computePlusMinus(rootAsOp,true); // recursive call to compute in here
				}
				
				// subtraction
				case MINUS: {
					return computePlusMinus(rootAsOp,false);
				}
				
				// scalar-multiply
				case SCALAR_MULTIPLY: {
					return null; // TODO	
				}
				
				// multiplication
				case TIMES: {
					return null; // TODO
				}
				
				// determinant
				case DETERMINANT: {
					return null; //TODO
				}
				
				// row-reduction
				case ROW_REDUCE: {
					return null; // TODO
				}
				
				// unrecognized operation
				default: {
					System.err.println("ERROR: Unrecognized operation"); // Should be unreachable code
					return null;
				}
			}
		}else if(root instanceof Countable){
			return null;
		}else{
			// should be unreachable code
			System.err.println("ERROR (compute): compute should not receive anything but countables or operations");
			return null;
		}
	}
	
	
	/** Gets the Numerical that served as the argument to an Operation
	 * 
	 * @param arg the result of a call to compute with <childOfOp> as an argument
	 * @param childOfOp the result of call to get[...]Arg() from an Operation 
	 * @return the Numerical that served as an argument to an Operation
	 */
	private static Numerical getNextArg(ParseNode arg,Numerical childOfOp){
		Numerical toReturn;
		if (arg ==null){
			toReturn = childOfOp;
		}else{
			Solution arg1Sol = arg.getSolution();
			toReturn = arg1Sol.getAnswer();
		}
		return toReturn;
	}
	
	
	/** Given a plus operator that includes its arguments, returns a ParseNode containing the Solution
	 * 
	 * @param op the plus operator
	 * @param isPlus true iff this is a plus operation. false iff this is a minus operation
	 * @return the ParseNode containing the solution and arguments to <op>
	 */
	private static ParseNode computePlusMinus(Operation op,boolean isPlus){
		if ((op.getFirstArg() == null || (op.getSecondArg() == null))){
			throw new IllegalArgumentException("ERROR: Plus requires two arguments"); // should be unreachable code
		}
		
		Numerical first = op.getFirstArg();     // this could return an Operation or a Countable
		Numerical second = op.getSecondArg();
		
		ParseNode firstArg = compute(first);          // this will return null if passed a Countable
		ParseNode secondArg= compute(second);
		
		Numerical arg1 = getNextArg(firstArg,first);  // b/c we need to actually compute, gets the Countable arguments
		Numerical arg2 = getNextArg(secondArg,first);
		
		if (!(arg1 instanceof Matrix) || !(arg2 instanceof Matrix)){
			throw new IllegalArgumentException("ERROR: Plus arguments must be matrices"); // should be unreachable code
		}
		
		Solution answer;
		if (isPlus){
			MM_PlusMinus plus = new MM_PlusMinus((Matrix) arg1, (Matrix) arg2,true); // calculate solution
			answer = plus.getSolution();					// get solution
		}else{ // is a minus
			MM_PlusMinus minus = new MM_PlusMinus((Matrix) arg1, (Matrix) arg2,false);
			answer = minus.getSolution();
		}
		return new ParseNode(answer,firstArg,secondArg);
	}
	
	
	/** Checks that the input computation was valid
	 * 
	 * @param input the list of Numericals making up the input
	 * @throws IllegalArgumentException if input is invalid
	 */
	protected static void checkValidInput(List<Numerical> input) throws IllegalArgumentException {
		if (input.size() == 0){
			throw new IllegalArgumentException("ERROR: Require expression to compute");
		}
		
		checkBrackets(input); // check that brackets are valid

		if (input.size() == 1){ // edge case
			if (input.get(0) instanceof Operation){
				if (((Operation) input.get(0)).isUnary()){
					throw new IllegalArgumentException("ERROR: Unary operator requires operand");
				}else{
					throw new IllegalArgumentException("ERROR: Binary operator requires two operands");
				}
			}else if(input.get(0) instanceof Bracket){
				throw new IllegalArgumentException("ERROR: Unpaired bracket");
			}else{
				throw new IllegalArgumentException("ERROR: Require expression to compute"); // maybe don't need to throw expection for this
			}
		}
		
		if (input.get(0) instanceof Operation && !((Operation) input.get(0)).isUnary()){
			throw new IllegalArgumentException("ERROR: Binary operation requires two operands");
		}
		
		Numerical last = null;
		for (Numerical numr : input){
			if (last != null && last instanceof Operation && numr instanceof Operation // check for two nonunary operators in a row
					&& !((Operation) numr).isUnary()){
				throw new IllegalArgumentException("ERROR: Binary operation requires two operands");
			}
			if (last != null && last instanceof Countable && numr instanceof Countable){ // check for adjacent Countables
				throw new IllegalArgumentException("ERROR: Two adjacent Countables");
			}
			if (last != null && last instanceof Operation && ((Operation) last).isUnary() && !(numr instanceof Countable)){ // check that each unary operator has operand
				if (numr instanceof Bracket && !((Bracket) numr).isOpen()){
					throw new IllegalArgumentException("ERROR: Unary operator requires operand");
				}
			}
			if (last != null && last instanceof Bracket && ((Bracket) last).isOpen() && numr instanceof Bracket && !((Bracket) numr).isOpen()){
				throw new IllegalArgumentException("ERROR: Empty brackets");
			}
			last = numr;
		}
		
		if (last instanceof Operation){ // edge case
			if (((Operation) last).isUnary()){
				throw new IllegalArgumentException("ERROR: Unary operator requires operand");
			}else{
				throw new IllegalArgumentException("ERROR: Binary operator requires two operands");
			}
		}
	}
	
	
	/** Checks that an input computation properly opens and closes brackets
	 * 
	 * @param input A series of Numericals representing a computation
	 * @throws IllegalArgumentException thrown if the brackets aren't valid
	 */
	protected static void checkBrackets(List<Numerical> input) throws IllegalArgumentException {
		boolean firstBracket = true;
		int openBrackets,closedBrackets;
		openBrackets = closedBrackets = 0;
		boolean isOpen;
		for (Numerical numr : input){
			if (numr instanceof Bracket){
				if (firstBracket && !((Bracket) numr).isOpen()){
					throw new IllegalArgumentException("ERROR: Close bracket without open bracket");
				}else{
					firstBracket = false;
				}
				
				isOpen = ((Bracket) numr).isOpen();
				if (isOpen){
					openBrackets++;
				}else{
					closedBrackets++;
				}
			}
		}
		
		if (openBrackets != closedBrackets){
			throw new IllegalArgumentException("ERROR: Every open bracket must have a close bracket and vice versa");
		}
	}
	
	
	/** Converts the input list of Numericals representing a sequence of operations into 
	 *  a tree where leaves are Computables and non-leaves are Operations. Operations lower in the tree
	 *  must be computed before operators higher in the tree. Note that the leaves, which
	 *  will inevitably be Countables, are implicit as they are
	 *  included in the Operations with no left or right children.
	 * 
	 * @param input the list of Numericals making up the input equation
	 * @return  Numerical that is the root of the parsed tree of Operations
	 */
	protected static Numerical createSortedTree(List<Numerical> input){
		input = removeOuterBrackets(input);
		
		if (input.size() == 0){
			return null;
		}else if(input.size() == 1){
			return input.get(0); //This must be a Countable
		}
		
		int prefOpIndex = findLeastPreferentialOp(input);

		// This code should be unreachable
		if (prefOpIndex == -1){
			System.err.println("ERROR: createSortedTree passed invalid input");	
		}
		
		List<Numerical> prev = new ArrayList<>(input.subList(0, prefOpIndex));
		List<Numerical> next = new ArrayList<>(input.subList(prefOpIndex+1, input.size()));
		Operation root = (Operation) input.get(prefOpIndex);
		root.setFirstArg(createSortedTree(prev));
		root.setSecondArg(createSortedTree(next));
		return root;
	}
	
	
	/** Returns the index of the operation that must be performed last among the operations present
	 *  in the computation. If there is no preference due to Operator rank, preference is 
	 *  set by list order. (ex: A + B + C -> B + C comes last). EXPECTS A SEQUENCE OF NUMERICALS
	 *  THAT IS NOT IN ITS ENTIRETY SURROUNDED BY A PAIR OF PARENS
	 *  
	 *  ALSO NOTE: this function assumes unary operators to be more preferential to binary operators,
	 *  and equally preferential to each other
	 * 
	 * @param input the list of numericals making up the input equation
	 * @return the index in the list of the operation that should be computed last
	 */
	protected static int findLeastPreferentialOp(List<Numerical> input){
		Numerical currentNumr;
		int maxRank = -1;
		int currRank;
		int toReturn = -1; // index to return
		int openBrackets = 0;
		boolean unaryHasMaxRank = false;
		for (int i = 0; i < input.size(); i++){
			currentNumr = input.get(i);
			if (currentNumr instanceof Bracket){
				if(((Bracket) currentNumr).isOpen()){
					openBrackets++;
				}else{
					openBrackets--;
				}
			}else{
				if (currentNumr instanceof Operation && openBrackets == 0){
					currRank = ((Operation) currentNumr).getRank();
					if (currRank >= maxRank){
						if (((Operation) currentNumr).isUnary()){
							
							// basically, we are saying that if only unary ops are found, the 1st one found is least preferential
							if (!unaryHasMaxRank){ 
								maxRank = currRank;
								toReturn = i;
							}
							
							unaryHasMaxRank = true;
						}else{
							unaryHasMaxRank = false;
							maxRank = currRank;
							toReturn = i;
						}
					}
				} 
			}
		}
		return toReturn;
	}
	
	
	/** If the input is begun by a bracket that is closed by a bracket at the end of the input, this removes
	 *  those outer brackets. EXPECTS VALID PARENTHESES, THAT IS EACH OPEN PARENS SHOULD HAVE A CLOSED PARENS
	 * 
	 * @param input the list of Numericals representing a computation
	 * @return the same list except without first and last brackets if they existed as a pair
	 */
	protected static List<Numerical> removeOuterBrackets(List<Numerical> input){
		if (input.size() == 0){
			return input;
		}
		if (!(input.get(0) instanceof Bracket)){ // computation not begun by a bracket
			return input;
		}else{
			int unclosedBrackets = 1;
			for (int i = 1; i < input.size()-1; i++){
				if (input.get(i) instanceof Bracket){
					if (((Bracket) input.get(i)).isOpen()){
						unclosedBrackets++;
					}else{
						unclosedBrackets--;
						if (unclosedBrackets == 0){
							return input;
						}
					}
				}
			}
			return new ArrayList<Numerical>(input.subList(1, input.size()-1));
		}
	}
	
}
