package io.conceptive.quarkus.plugin.runconfig.executionfacade.gradle;

import com.intellij.openapi.externalSystem.model.task.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import java.util.*;

/**
 * Listener that gets notified, if a gradle build produced text
 *
 * @author w.glanzer, 23.05.2020
 */
public class GradleNotificationListener extends ExternalSystemTaskNotificationListenerAdapter
{
  private static final String _DEBUGGER_READY_STRING = "Listening for transport dt_socket at address";
  private static final Map<String, Runnable> executeOnFinish = new HashMap<>();

  @Override
  public void onTaskOutput(@NotNull ExternalSystemTaskId id, @NotNull String text, boolean stdOut)
  {
    synchronized (executeOnFinish)
    {
      if (id.getProjectSystemId().equals(GradleConstants.SYSTEM_ID) && text.contains(_DEBUGGER_READY_STRING))
      {
        Runnable onReady = executeOnFinish.get(id.getIdeProjectId());
        if (onReady != null)
          onReady.run();
      }
    }
  }

  /**
   * Adds a new handler to this NotificationListener that gets notified, if a debugger can be connected
   *
   * @param pProjectID ID of the Project
   * @param pRunnable  Runnable to execute
   */
  public static void addHandler(@NotNull String pProjectID, @NotNull Runnable pRunnable)
  {
    synchronized (executeOnFinish)
    {
      executeOnFinish.put(pProjectID, pRunnable);
    }
  }
}