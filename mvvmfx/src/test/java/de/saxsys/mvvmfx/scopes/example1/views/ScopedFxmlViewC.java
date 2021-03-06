package de.saxsys.mvvmfx.scopes.example1.views;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.internal.viewloader.example.TestScope1;
import de.saxsys.mvvmfx.internal.viewloader.example.TestScope2;
import de.saxsys.mvvmfx.scopes.example1.Example1Scope1;
import de.saxsys.mvvmfx.scopes.example1.Example1Scope2;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ScopedFxmlViewC implements FxmlView<ScopedViewModelC> {

    @InjectViewModel
    public ScopedViewModelC viewModel;

    @FXML
    public VBox root;

    @InjectContext
    public Context context;

    public ScopedFxmlViewD subViewDController;
    public ScopedFxmlViewD subViewDWithoutContextController;

    public void initialize() {
        ScopedViewModelD scopedViewModelD = new ScopedViewModelD();
        ViewTuple<ScopedFxmlViewD, ScopedViewModelD> load = FluentViewLoader.fxmlView(ScopedFxmlViewD.class)
                .viewModel(scopedViewModelD)
                .context(context)
                .providedScopes(new Example1Scope2())
                .load();
        root.getChildren().add(load.getView());
        subViewDController = load.getCodeBehind();
    }

    public void loadWrongScopedView() {
        ViewTuple<ScopedFxmlViewD, ScopedViewModelD> load2 = FluentViewLoader.fxmlView(ScopedFxmlViewD.class).load();
        root.getChildren().add(load2.getView());
        subViewDWithoutContextController = load2.getCodeBehind();
    }

    public void loadCorrectScopedView() {
        ViewTuple<ScopedFxmlViewD, ScopedViewModelD> load2 = FluentViewLoader.fxmlView(ScopedFxmlViewD.class)
                .providedScopes(new Example1Scope1(), new Example1Scope2())
                .load();
        root.getChildren().add(load2.getView());
        subViewDWithoutContextController = load2.getCodeBehind();
    }

}
