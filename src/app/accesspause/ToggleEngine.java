package app.accesspause;

/** Save before mutating Android settings. Keep recovery data after every failure. */
public final class ToggleEngine {
    public interface Backend {
        String services(); int enabled(); boolean pending();
        String savedServices(); int savedEnabled();
        void save(String services, int enabled);
        void services(String value); void enabled(int value); void complete();
    }
    public static String toggle(Backend b) {
        if (b.pending()) {
            String services = b.savedServices(); int enabled = b.savedEnabled();
            b.services(services); b.enabled(enabled);
            if (!services.equals(b.services()) || enabled != b.enabled())
                throw new IllegalStateException("Android did not accept the restore. Your saved list is still available; try again or use Accessibility settings.");
            b.complete();
            return "Saved accessibility services restored";
        }
        String services = b.services(); int enabled = b.enabled();
        if (services.isEmpty() && enabled == 0) return "No enabled accessibility services to pause";
        b.save(services, enabled);
        b.enabled(0); b.services("");
        if (!b.services().isEmpty() || b.enabled() != 0)
            throw new IllegalStateException("Android did not accept the pause. Your original list is saved. Tap Restore to recover it.");
        return "Accessibility services paused";
    }
}
