package com.tyron.code.desktop.ui.control;

import com.tyron.code.project.util.ThreadPoolFactory;
import org.jetbrains.annotations.NotNull;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.kordamp.ikonli.Ikon;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Button with an on-click runnable action.
 *
 * @author Matt Coley
 */
public class ActionButton extends Button implements Tooltipable {
	private static final ExecutorService service = ThreadPoolFactory.newSingleThreadExecutor("async-button-action");

	/**
	 * @param text
	 * 		Button display text.
	 * @param action
	 * 		Action to run on-click.
	 */
	public ActionButton(String text, Runnable action) {
		super(text);
		setOnAction(e -> wrap(e, action));
	}

	/**
	 * @param icon
	 * 		Button display icon.
	 * @param action
	 * 		Action to run on-click.
	 */
	public ActionButton(Ikon icon, Runnable action) {
		this(new FontIconView(icon), action);
	}

	/**
	 * @param graphic
	 * 		Button display graphic.
	 * @param action
	 * 		Action to run on-click.
	 */
	public ActionButton(Node graphic, Runnable action) {
		setGraphic(graphic);
		setOnAction(e -> wrap(e, action));
	}

	/**
	 * @param icon
	 * 		Button display icon.
	 * @param text
	 * 		Button display text.
	 * @param action
	 * 		Action to run on-click.
	 */
	public ActionButton(Ikon icon, ObservableValue<String> text, Runnable action) {
		setGraphic(new FontIconView(icon));
		textProperty().bind(text);
		setOnAction(e -> wrap(e, action));
	}

	/**
	 * @param graphic
	 * 		Button display graphic.
	 * @param text
	 * 		Button display text.
	 * @param action
	 * 		Action to run on-click.
	 */
	public ActionButton(Node graphic, ObservableValue<String> text, Runnable action) {
		setGraphic(graphic);
		textProperty().bind(text);
		setOnAction(e -> wrap(e, action));
	}

	/**
	 * @param text
	 * 		Button display text.
	 * @param action
	 * 		Action to run on-click.
	 */
	public ActionButton(ObservableValue<String> text, Runnable action) {
		textProperty().bind(text);
		setOnAction(e -> wrap(e, action));
	}

	/**
	 * Only allow the action to be run once.
	 *
	 * @return Self.
	 */
	@NotNull
	public ActionButton once() {
		EventHandler<ActionEvent> onAction = getOnAction();
		if (onAction == null)
			throw new IllegalArgumentException("No action set");
		setOnAction(e -> {
			onAction.handle(e);
			setDisable(true);
			setOnAction(null);
		});
		return this;
	}

	/**
	 * Run the provided action asynchronously.
	 * Do note that this will no longer run the action on the FX thread.
	 *
	 * @return Self.
	 */
	@NotNull
	public ActionButton async() {
		EventHandler<ActionEvent> onAction = getOnAction();
		if (onAction == null)
			throw new IllegalArgumentException("No action set");
		setOnAction(e -> CompletableFuture.runAsync(() -> onAction.handle(e), service));
		return this;
	}

	/**
	 * @param width
	 * 		Width to assign to button.
	 *
	 * @return Self.
	 */
	@NotNull
	public ActionButton width(double width) {
		setMinWidth(width);
		setPrefWidth(width);
		return this;
	}

	private static void wrap(ActionEvent e, Runnable action) {
		// This stops the input from 'bleeding' through to parent control handlers.
		//  - Useful for when the button is used in 'x.setGraphic(button)' scenarios
		e.consume();

		// Then run the action.
		action.run();
	}
}