package de.saxsys.jfx.exampleapplication.view.maincontainer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.FluentViewLoader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;

import de.saxsys.jfx.exampleapplication.view.personlogin.PersonLoginView;
import de.saxsys.jfx.exampleapplication.view.personwelcome.PersonWelcomeView;
import de.saxsys.jfx.exampleapplication.viewmodel.maincontainer.MainContainerViewModel;
import de.saxsys.jfx.exampleapplication.viewmodel.personwelcome.PersonWelcomeViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.internal.viewloader.View;
import de.saxsys.mvvmfx.utils.viewlist.ViewListCellFactory;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.notifications.NotificationObserver;
import de.saxsys.mvvmfx.ViewTuple;

/**
 * Main View which creates the necessary subviews, and manages them. Does not need a concrete Viewmodel, so it is typed
 * with the inferface. Have a careful look on the FXML file to see, how to include different views into a MasterView.
 */
public class MainContainerView implements FxmlView<MainContainerViewModel>, Initializable {
	
	@FXML
	// Injection of the login which is declared in the FXML File
	private StackPane loginView; // Value injected by FXMLLoader
	
	@FXML
	// Inject the Code behind instance of the loginView by using the
	// nameconvention ...Controller
	private PersonLoginView loginViewController;
	
	@FXML
	// Inject the Code behind instance of the ListView
	private ListView<Integer> personWelcomeListView;
	
	@Inject
	// Notification Center
	private NotificationCenter notificationCenter;
	
	
	@InjectViewModel
	private MainContainerViewModel viewModel;
	
	
	private Map<Integer, ViewTuple<PersonWelcomeView, PersonWelcomeViewModel>> viewMap = new HashMap<>();
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Listen for close notifications
		notificationCenter.addObserverForName("hidePersonWelcome",
				new NotificationObserver() {
					@Override
					public void receivedNotification(String key,
							Object... objects) {
						int personIdToHide = (int) objects[0];
						viewModel.displayedPersonsProperty().remove(
								new Integer(personIdToHide));
					}
				});
		
		// When the login button of the loginView, the pickedPersonProperty is
		// going to have the index of the selected person
		loginViewController.getViewModel().loggedInPersonIdProperty()
				.addListener(new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number oldValue, Number newValue) {
						int id = newValue.intValue();
						viewModel.displayedPersonsProperty().add(id);
					}
				});
		
		// Configure List with views
		personWelcomeListView.setCellFactory(new ViewListCellFactory<Integer>() {
			@Override
			public ViewTuple<? extends View, ? extends ViewModel> map(Integer element) {
				if (!viewMap.containsKey(element)) {
					ViewTuple<PersonWelcomeView, PersonWelcomeViewModel> loadedViewTuple
					= FluentViewLoader.fxmlView(PersonWelcomeView.class).load();
					
					PersonWelcomeView codeBehind = loadedViewTuple.getCodeBehind();
					
					codeBehind.getViewModel()
							.setPersonId(element);
					
					viewMap.put(element, loadedViewTuple);
				}
				
				return viewMap.get(element);
			}
		});
		
		// Bind list
		personWelcomeListView.itemsProperty().bind(
				viewModel.displayedPersonsProperty());
	}
}
