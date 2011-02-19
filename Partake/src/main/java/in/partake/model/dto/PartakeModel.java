package in.partake.model.dto;

public abstract class PartakeModel<T extends PartakeModel<?>> {
    private volatile boolean frozen;
    
    protected PartakeModel() {
        this.frozen = false;
    }
    
    public abstract Object getPrimaryKey();
    public abstract T copy();
    
    protected void checkFrozen() {
        if (frozen) { throw new UnsupportedOperationException(); }
    }
    
    public boolean isFrozen() {
        return frozen;
    }
    
    @SuppressWarnings("unchecked")
    public T freeze() {
        this.frozen = true;
        return (T)this;
    }
    
    public void assureFreeze() {
        this.frozen = true;
    }
}
