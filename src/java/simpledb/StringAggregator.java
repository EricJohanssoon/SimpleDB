package simpledb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;

    private HashMap<Field, ArrayList<String>> aggregateMap = new HashMap<>();
    private HashMap<Field, String> aggregateResult = new HashMap<>();
    private HashMap<Field, Integer> countResult = new HashMap<>();

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;
        this.what = what;
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
        // fetch the group by field.
        Field field = tup.getField(gbfield);

        // fetch the aggregating string value.
        StringField stringField = (StringField) tup.getField(afield);
        String stringValue = stringField.getValue();

        // If the HashTable contains the group-by field, add the aggregating string to the list.
        if (aggregateMap.containsKey(field)){
            ArrayList<String> stringsList = aggregateMap.get(field);
            stringsList.add(stringValue);
        }
        // Else create new list and add new key-value pair.
        else {
            ArrayList<String> newList = new ArrayList<>();
            newList.add(stringValue);
            aggregateMap.put(field, newList);
        }
    }

    private void aggreagtionOperation(){
        // For every key-value pair in the aggragateTable.
        aggregateMap.forEach((key, value) -> {
            switch (this.what){
                case MIN:
                    aggregateResult.put(key, Collections.min(value));
                    break;

                case MAX:
                    aggregateResult.put(key, Collections.max(value));
                    break;

                case COUNT:
                    countResult.put(key, value.size());
                    break;
            }
        });
    }



    /**
     * Create a OpIterator over group aggregate results.
     *
     * @return a OpIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public OpIterator iterator() {
        // some code goes here
        // Creating the TupleDesc
        this.aggreagtionOperation();

        Type[] types;
        String[] fields;
        TupleDesc td;
        ArrayList<Tuple> tupleList;

        // If the operation is COUNT and we want a int as aggregate result
        if(this.what == Op.COUNT){
            if (this.gbfield == NO_GROUPING){
                types = new Type[]{Type.INT_TYPE};
                fields = new String[]{"aggregateVal"};
            }
            else {
                types = new Type[]{this.gbfieldtype, Type.INT_TYPE};
                fields = new String[]{"groupVal", "aggregateVal"};
            }
            td = new TupleDesc(types, fields);
            tupleList = new ArrayList<>();

            countResult.forEach((key, value) -> {
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

        }

        // If the operation is MAX / MIN and we want a String as aggregating result
        else {

            if (this.gbfield == NO_GROUPING){
                types = new Type[]{Type.STRING_TYPE};
                fields = new String[]{"aggregateVal"};
            }
            else {
                types = new Type[]{this.gbfieldtype, Type.STRING_TYPE};
                fields = new String[]{"groupVal", "aggregateVal"};
            }
            td = new TupleDesc(types, fields);
            tupleList = new ArrayList<>();

            aggregateResult.forEach((key, value) -> {
                Tuple tuple = new Tuple(td);

                if (this.gbfield == NO_GROUPING){
                    tuple.setField(0, new StringField(value,64));
                    tupleList.add(tuple);
                }
                else{
                    tuple.setField(0, key);
                    tuple.setField(1, new StringField(value, 64));
                    tupleList.add(tuple);
                }
            });
        }

        return new TupleIterator(td, tupleList);


    }

}
