package io.conceptive.quarkus.plugin.runconfig.factory;

import com.google.common.base.Strings;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.*;
import org.jetbrains.idea.maven.execution.*;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.awt.*;

/**
 * General-Tab
 *
 * @author w.glanzer, 21.04.2020
 */
class ParametersSettingsEditorImpl extends SettingsEditor<RunConfigImpl>
{

  private final _WorkingDirectoryComponent workingDirComponent;
  private final LabeledComponent<RawCommandLineEditor> vmOptions;

  public ParametersSettingsEditorImpl(@NotNull Project pProject)
  {
    workingDirComponent = new _WorkingDirectoryComponent(pProject);
    vmOptions = new LabeledComponent<>();
    vmOptions.setComponent(new RawCommandLineEditor());
    vmOptions.setLabelLocation(BorderLayout.WEST);
    vmOptions.setText("VM Options");
  }

  @Override
  protected void resetEditorFrom(@NotNull RunConfigImpl pImpl)
  {
    workingDirComponent.setValue(Strings.nullToEmpty(pImpl.getOptions().getWorkingDir()));
    vmOptions.getComponent().setText(Strings.nullToEmpty(pImpl.getOptions().getVmOptions()));
  }

  @Override
  protected void applyEditorTo(@NotNull RunConfigImpl pImpl) throws ConfigurationException
  {
    pImpl.getOptions().setWorkingDir(workingDirComponent.getValue());
    pImpl.getOptions().setVmOptions(vmOptions.getComponent().getText());
  }

  @NotNull
  @Override
  protected JComponent createEditor()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(workingDirComponent);
    panel.add(Box.createVerticalStrut(4));
    panel.add(vmOptions);
    return panel;
  }

  /**
   * Component that displays the workingDirectory and a module/filechooser
   */
  private static class _WorkingDirectoryComponent extends LabeledComponent<JPanel>
  {
    private final TextFieldWithBrowseButton textField = new TextFieldWithBrowseButton();

    public _WorkingDirectoryComponent(@NotNull Project pProject)
    {
      FixedSizeButton projectTreeButton = new FixedSizeButton();
      projectTreeButton.setIcon(AllIcons.Nodes.Module);
      JPanel container = new JPanel(new BorderLayout());
      container.add(textField, BorderLayout.CENTER);
      container.add(projectTreeButton, BorderLayout.EAST);
      setComponent(container);
      setLabelLocation(BorderLayout.WEST);
      setText("Working Directory");
      textField.addBrowseFolderListener("Select Quarkus Module", "", pProject, new MavenPomFileChooserDescriptor(pProject));
      MavenSelectProjectPopup.attachToWorkingDirectoryField(MavenProjectsManager.getInstance(pProject), textField.getTextField(), projectTreeButton, null);
    }

    public String getValue()
    {
      return textField.getText();
    }

    public void setValue(@Nullable String pValue)
    {
      textField.setText(pValue);
    }
  }

}
