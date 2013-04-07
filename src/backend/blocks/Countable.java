package backend.blocks;

/** A number value Numerical. This includes scalar values
 *  and matrices
 */
abstract class Countable implements Numerical{
	protected boolean _isDouble;
	protected boolean _isFraction;
	
	/** Retrieves fraction status of this countable
	 * 
	 * @return true if the value(s) of this object should be represented
	 * as fraction(s)
	 */
	public boolean isFraction(){
		return _isFraction;
	}
	
	/** Retrieves double status of this countable
	 * 
	 * @return true if the value(s) of this object should be represented
	 * as decimal(s)
	 */
	public boolean isDouble(){
		return _isDouble;
	}

}