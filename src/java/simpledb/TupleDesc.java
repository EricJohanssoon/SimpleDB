package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    TDItem TDArray[];
    Type[] typeAr;
    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;


        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here
        return Arrays.stream(this.TDArray).iterator();
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        this.typeAr = typeAr;
        TDItem[] tdArray = new TDItem[typeAr.length];
        for (int i = 0; i<typeAr.length; i++) {
            tdArray[i] = new TDItem(typeAr[i], fieldAr[i]);
        }
        this.TDArray = tdArray;
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
        this.typeAr = typeAr;
        TDItem[] tdArray = new TDItem[typeAr.length];
        for (int i = 0; i<typeAr.length; i++) {

            tdArray[i] = new TDItem(typeAr[i], "unnamed");
        }
        this.TDArray = tdArray;

    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return this.TDArray.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
        if(i < 0 || i > TDArray.length-1){
            throw new NoSuchElementException();
        }
        else{
            return TDArray[i].fieldName;
        }
    }

    public Type[] getTypeAr(){
        return this.typeAr;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // some code goes here
        if(i < 0 || i > TDArray.length-1){
            throw new NoSuchElementException();
        }
        else{
            return TDArray[i].fieldType;
        }
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
        for (int i = 0; i < this.numFields(); i++){
            if (TDArray[i].fieldName.equals(name)){
                return i;
            }
        }
        throw new NoSuchElementException(name + " not found");
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
        int integerCounter = 0;
        int stringCounter = 0;
        for (TDItem tdItem : TDArray) {
            if (tdItem.fieldType.equals(Type.INT_TYPE)) {
                integerCounter++;
            } else if (tdItem.fieldType.equals(Type.STRING_TYPE)) {
                stringCounter++;
            }
        }
        return (integerCounter*Type.INT_TYPE.getLen() + stringCounter*Type.STRING_TYPE.getLen());
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here
        Type [] td1Types = new Type[td1.TDArray.length];
        Type [] td2Types = new Type[td2.TDArray.length];

        String [] td1Names = new String[td1.TDArray.length];
        String [] td2Names = new String[td2.TDArray.length];

        int mergedLen = td1.TDArray.length+td2.TDArray.length;
        Type[] mergedTypeAr = new Type[mergedLen];
        String[] mergedNameAr = new String[mergedLen];

        int j = 0;
        for (int i = 0; i < td1.TDArray.length; i++){
            mergedTypeAr[i] = td1.TDArray[i].fieldType;
            mergedNameAr[i] = td1.TDArray[i].fieldName;
            j++;
        }

        for (int i = 0; i < td2.numFields(); i++){
            mergedTypeAr[j] = td2.TDArray[i].fieldType;
            mergedNameAr[j] = td2.TDArray[i].fieldName;
            j++;
        }

        return new TupleDesc(mergedTypeAr, mergedNameAr);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they have the same number of items
     * and if the i-th type in this TupleDesc is equal to the i-th type in o
     * for every i.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */

    public boolean equals(Object o) {
        // some code goes here
        if (this == o) {
            return true;
        }

        if (o instanceof TupleDesc) {
            TupleDesc cmp = (TupleDesc) o;
            if (this.numFields() == cmp.numFields()) {
                for (int i = 0; i < this.numFields(); i++) {
                    if (this.TDArray[i].fieldType != cmp.TDArray[i].fieldType) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.numFields() - 1; i++){
            sb.append(TDArray[i].fieldType);
            sb.append("(");
            sb.append(TDArray[i].fieldName);
            sb.append("),");
        }
        sb.append(TDArray[this.numFields()-1].fieldType);
        sb.append("(");
        sb.append(TDArray[this.numFields()-1].fieldName);
        sb.append(")");

        return sb.toString();
    }
}
