package simpledb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {
    TupleDesc description;
    Field[] fields;
    int numberOfFields;
    RecordId rid;


    private static final long serialVersionUID = 1L;

    /**
     * Create a new tuple with the specified schema (type).
     *
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        this.description = td;
        this.numberOfFields = td.numFields();
        Field[] myTuple = new Field[this.numberOfFields];
        int counter = 0;
        for (TupleDesc.TDItem item : td.TDArray)
        {
            if (item.fieldType.equals(Type.INT_TYPE)) {
                myTuple[counter] = new IntField(0);
            }
            else if (item.fieldType.equals(Type.STRING_TYPE)) {
                myTuple[counter] = new StringField("null",64);
            }
            else{
                System.out.println("You need to have either strings or ints as type");
            }
            counter++;
        }
        this.fields = myTuple;
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        return this.description;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
        return this.rid;
    }

    /**
     * Set the RecordId information for this tuple.
     *
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        this.rid = rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     *
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    public void setField(int i, Field f) {
        if (fields.length>i && i>=0){
            this.fields[i] = f;
        }
        else {
            throw new ArrayIndexOutOfBoundsException("This index is to high:");
        }

    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     *
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        if (fields.length > i && i >= 0){
            return this.fields[i];
        }
        else {
            System.out.println("Fields.len = " + fields.length);
            System.out.println("i = "+ i);
            throw new ArrayIndexOutOfBoundsException("This index is to high or to low:");
        }

    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     *
     * column1\tcolumn2\tcolumn3\t...\tcolumnN
     *
     * where \t is any whitespace (except a newline)
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (Field f : this.fields){
            sb.append(prefix);
            sb.append(f.toString());
            prefix = " ";
        }
        return sb.toString();
    }

    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {
        return Arrays.stream(fields).iterator();
    }

    /**
     * reset the TupleDesc of this tuple (only affecting the TupleDesc)
     * */
    public void resetTupleDesc(TupleDesc td)
    {
        this.description = td;
    }
}
