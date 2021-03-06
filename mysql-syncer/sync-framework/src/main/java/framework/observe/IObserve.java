package framework.observe;

/**
 * @author Ganziwen
 * @version 1.0
 * @ClassName IObserve
 * @Description
 * @date 2021/10/10 11:34
 */
public interface IObserve<Context> {
    boolean preUpdate(Context context);

    void update(Context context);
}
