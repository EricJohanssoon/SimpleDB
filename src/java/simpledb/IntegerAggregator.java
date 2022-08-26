package simpledb;



import java.util.*;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;

    private HashMap<Field, ArrayList<Integer>> aggregateMap = new HashMap<>();
    private HashMap<Field, Integer> aggregateResult = new HashMap<>();


    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;
        this.what = what;
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // fetch the group by field.
        Field field;
        if (gbfield != NO_GROUPING){
            field = tup.getField(gbfield);
        }
        else {
            field = null;
        }
        // fetch the aggregating integer value.
        IntField intField = (IntField) tup.getField(afield);
        Integer intValue = intField.getValue();

        // If the HashTable contains the group-by field, add the aggregating int to the list.
        if (aggregateMap.containsKey(field)){
            ArrayList<Integer> intList = aggregateMap.get(field);
            intList.add(intValue);
        }
        // Else create new list and add new key-value pair.
        else {
            ArrayList<Integer> newList = new ArrayList<>();
            newList.add(intValue);
            aggregateMap.put(field, newList);
        }
    }

    private void aggregationOperation(){
        // For every key-value pair in the aggragateTable.
        aggregateMap.forEach((key, value) -> {
            switch (this.what){
                case MIN:
                    aggregateResult.put(key, Collections.min(value));
                    break;

                case MAX:
                    aggregateResult.put(key, Collections.max(value));
                    break;

                case AVG:
                    int sum = 0;
                    for(Integer term : value){
                        sum += term;
                    }
                    int avg = sum / value.size();
                    aggregateResult.put(key, avg);
                    break;

                case SUM:
                    int tot = 0;
                    for (Integer term : value){
                        tot += term;
                    }
                    aggregateResult.put(key, tot);
                    break;

                case COUNT:
                    aggregateResult.put(key, value.size());
                    break;
            }
        });
    }

    /**
     * Create a OpIterator over group aggregate results.
     * 
     * @return a OpIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public OpIterator iterator() {
        // some code goes here
        // Creating the TupleDesc
        this.aggregationOperation();

        Type[] types;
        String[] fields;

        if (this.gbfield == NO_GROUPING){
            types = new Type[]{Type.INT_TYPE};
            fields = new String[]{"aggregateVal"};
        }
        else {
            types = new Type[]{this.gbfieldtype, Type.INT_TYPE};
            fields = new String[]{"groupVal", "aggregateVal"};
        }
        TupleDesc td = new TupleDesc(types, fields);
        ArrayList<Tuple> tupleList = new ArrayList<>();

        aggregateResult.forEach((key, value) -> {
            Tuple tuple = new Tuple(td);

            if (this.gbfield == NO_GROUPING){
                tuple.setField(0, new IntField(value));
                tupleList.add(tuple);
            }
            else{
                tuple.setField(0, key);
                tuple.setField(1, new IntField(value));
                tupleList.add(tuple);
            }
        });

        return new TupleIterator(td, tupleList);


    }

}
