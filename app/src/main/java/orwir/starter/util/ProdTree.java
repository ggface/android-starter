package orwir.starter.util;

import timber.log.Timber;

public class ProdTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        // TODO: 2017-07-31 handle errors with some analytics tool
    }

}
