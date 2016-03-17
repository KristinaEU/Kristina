package owlSpeak.engine.his.interfaces;

/**
 * Interface defining the methods of a History object. 
 * 
 * TODO Needs to be augmented in order make it owl persistent
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 *
 */
public interface IHistory {
		    
	public boolean __eq__(IHistory otherHistory);
		    
	public void Update(IPartition partition,IUserAction userAction, Object sysAction);

	public IHistory Copy ();
		    
	public String __str__();
}
