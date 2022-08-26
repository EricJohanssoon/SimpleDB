package simpledb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HeapFileIterator extends AbstractDbFileIterator{

    TransactionId tid;
    int fileId;
    Iterator<Tuple> currentIterator = null;
    int pageNr = 0;
    int nrOfPages;
    boolean close;

    public HeapFileIterator(TransactionId tid, int fileId, int nrOfPages){
        this.tid = tid;
        this.fileId = fileId;
        this.nrOfPages = nrOfPages;
        this.close = true;
    }


    @Override
    public Tuple readNext() throws DbException, TransactionAbortedException {
        if (close){
            return null;
        }
        if (currentIterator==null){
            currentIterator = createPageIterator(pageNr);
        }
        if (currentIterator.hasNext()){
            return currentIterator.next();
        }
        else if (pageNr<nrOfPages){
            currentIterator = createPageIterator(pageNr++);
            readNext();
        }
        return null;
    }

    @Override
    public boolean hasNext() throws DbException, TransactionAbortedException {
        if (close){
            return false;
        }
        if (currentIterator==null) {
            currentIterator = createPageIterator(pageNr);
        }
        return currentIterator.hasNext();
    }

    @Override
    public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
        return super.next();
    }

    @Override
    public void open() throws DbException, TransactionAbortedException {
        this.close = false;
    }
    public void close() {
        close = true;
    }

    @Override
    public void rewind() throws DbException, TransactionAbortedException {
        this.pageNr = 0;
        currentIterator = createPageIterator(this.pageNr);
    }

    private Iterator<Tuple> createPageIterator(int pageNumber) throws DbException{
        HeapPageId pid = new HeapPageId(this.fileId, pageNumber);
        try {
            HeapPage page = (HeapPage) Database.getBufferPool().getPage(this.tid, pid, Permissions.READ_WRITE);
            return page.iterator();
        }
        catch (Exception e){
            System.out.println(e);
        }
        throw new DbException("No iterator made");
    }
}

