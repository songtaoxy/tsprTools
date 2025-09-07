package com.st.modules.thread.framework.v4;


public final class BizContextHolder {
    private static final ThreadLocal<BizContext> TL = new ThreadLocal<BizContext>();
    public static BizContext get(){ return TL.get(); }
    public static void set(BizContext ctx){ TL.set(ctx); }
    public static void clear(){ TL.remove(); }
    public static Scope with(BizContext ctx){ set(ctx); return new Scope(); }
    public static final class Scope implements AutoCloseable { public void close(){ BizContextHolder.clear(); } }
}

