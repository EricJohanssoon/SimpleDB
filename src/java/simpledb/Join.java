package simpledb;

import java.util.*;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends Operator {

    private static final long serialVersionUID = 1L;

    private JoinPredicate joinPredicate;
    private OpIterator child1;
    private OpIterator child2;

    private Tuple currentOuterTuple = null;

    /**
     * Constructor. Accepts two children to join and the predicate to join them
     * on
     * 
     * @param p
     *            The predicate to use to join the children
     * @param child1
     *            Iterator for the left(outer) relation to join
     * @param child2
     *            Iterator for the right(inner) relation to join
     */
    public Join(JoinPredicate p, OpIterator child1, OpIterator child2) {
        // some code goes here
        this.joinPredicate = p;
        this.child1 = child1;
        this.child2 = child2;
    }

    public JoinPredicate getJoinPredicate() {
        // some code goes here
        return this.joinPredicate;
    }

    /**
     * @return
     *       the field name of join field1. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField1Name() {
        // some code goes here
        // TODO what does "Should be quantified by alias or table name" mean? Is it tablename.columnName?
        int fieldIndex = this.joinPredicate.getField1();
        TupleDesc tupleDesc = this.child1.getTupleDesc();
        String fieldname = tupleDesc.getFieldName(fieldIndex);
        return fieldname;
    }

    /**
     * @return
     *       the field name of join field2. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField2Name() {
        // some code goes here
        // TODO what does "Should be quantified by alias or table name" mean? Is it tablename.columnName?
        int fieldIndex = this.joinPredicate.getField2();
        TupleDesc tupleDesc = this.child2.getTupleDesc();
        String fieldname = tupleDesc.getFieldName(fieldIndex);
        return fieldname;
    }

    /**
     * @see simpledb.TupleDesc#merge(TupleDesc, TupleDesc) for possible
     *      implementation logic.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        TupleDesc td1 = child1.getTupleDesc();
        TupleDesc td2 = child2.getTupleDesc();
        return TupleDesc.merge(td1, td2);
    }

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
        super.open();
        child1.open();
        child2.open();
    }

    public void close() {
        // some code goes here
        super.close();
        child1.close();
        child2.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        child1.rewind();
        child2.rewind();
    }

    /**
     * Returns the next tuple generated by the join, or null if there are no
     * more tuples. Logically, this is the next tuple in r1 cross r2 that
     * satisfies the join predicate. There are many possible implementations;
     * the simplest is a nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of Join
     * are simply the concatenation of joining tuples from the left and right
     * relation. Therefore, if an equality predicate is used there will be two
     * copies of the join attribute in the results. (Removing such duplicate
     * columns can be done with an additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     * 
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // While there exist more outer tuples or not done iterating current one
        while(child1.hasNext() || currentOuterTuple != null){
            Tuple t1;
            // If done iterating outer tuple
            if (currentOuterTuple == null){
                currentOuterTuple = child1.next();
                t1  = currentOuterTuple;
            }
            // Else continue with outer tuple
            else{
                t1 = currentOuterTuple;
            }
            // Iterate through all inner tuples
            while (child2.hasNext()){
                Tuple t2 = child2.next();
                if(this.joinPredicate.filter(t1,t2)){
                    return mergeTuple(t1,t2);
                }
            }
            // Done iterating outer tuple
            currentOuterTuple = null;
            child2.rewind();
        }
        return null;

   }

    @Override
    public OpIterator[] getChildren() {
        // some code goes here

        // Thought, the selection operator only takes one as input (compared to join which has 2)
        OpIterator[] children = new OpIterator[]{this.child1, this.child2};
        return children;
    }

    @Override
    public void setChildren(OpIterator[] children) {
        // some code goes here
        this.child1 = children[0];
        this.child2 = children[1];
    }

    // Merging two tuples by appending t2 to t1.
    private Tuple mergeTuple(Tuple t1, Tuple t2){
        int t1Length = t1.numberOfFields;
        int t2Length = t2.numberOfFields;
        Tuple mergedTuple = new Tuple(this.getTupleDesc());

        for (int i = 0; i < t1Length; i++){
            mergedTuple.setField(i, t1.getField(i));
        }

        for (int i = 0; i < t2Length; i++){
            mergedTuple.setField(t1Length + i, t2.getField(i));
        }

        return mergedTuple;
    }


}

