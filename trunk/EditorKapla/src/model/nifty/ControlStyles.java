package model.nifty;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.PopupBuilder;
import de.lessvoid.nifty.builder.StyleBuilder;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.tools.Color;

public class ControlStyles {

	private Nifty nifty;

	public ControlStyles(final Nifty nifty) {
		this.nifty = nifty;
	}
	public void registerMenuButtonHintStyle() {
		new StyleBuilder() {

			{
				id("special-hint");
				base("nifty-panel-bright");
				childLayoutCenter();
				onShowEffect(new EffectBuilder("fade") {

					{
						length(150);
						effectParameter("start", "#0");
						effectParameter("end", "#d");
						inherit();
						neverStopRendering(true);
					}
				});
				onShowEffect(new EffectBuilder("move") {

					{
						length(150);
						inherit();
						neverStopRendering(true);
						effectParameter("mode", "fromOffset");
						effectParameter("offsetY", "-15");
					}
				});
				onCustomEffect(new EffectBuilder("fade") {

					{
						length(150);
						effectParameter("start", "#d");
						effectParameter("end", "#0");
						inherit();
						neverStopRendering(true);
					}
				});
				onCustomEffect(new EffectBuilder("move") {

					{
						length(150);
						inherit();
						neverStopRendering(true);
						effectParameter("mode", "toOffset");
						effectParameter("offsetY", "-15");
					}
				});
			}
		}.build(nifty);

		new StyleBuilder() {

			{
				id("special-hint#hint-text");
				base("base-font");
				alignLeft();
				valignCenter();
				textHAlignLeft();
				color(new Color("#000f"));
			}
		}.build(nifty);
	}

	public void registerStyles() {
		new StyleBuilder() {

			{
				id("base-font-link");
				base("base-font");
				color("#8fff");
				interactOnRelease("$action");
				onHoverEffect(new HoverEffectBuilder("changeMouseCursor") {

					{
						effectParameter("id", "hand");
					}
				});
			}
		}.build(nifty);

		new StyleBuilder() {

			{
				id("creditsImage");
				alignCenter();
			}
		}.build(nifty);

		new StyleBuilder() {

			{
				id("creditsCaption");
				font("Interface/verdana-48-regular.fnt");
				width("100%");
				textHAlignCenter();
			}
		}.build(nifty);

		new StyleBuilder() {

			{
				id("creditsCenter");
				base("base-font");
				width("100%");
				textHAlignCenter();
			}
		}.build(nifty);
	}

	public void registerConsolePopup() {
		new PopupBuilder("consolePopup") {

			{
				childLayoutAbsolute();
				panel(new PanelBuilder() {

					{
						childLayoutCenter();
						width("100%");
						height("100%");
						alignCenter();
						valignCenter();
						control(new ConsoleBuilder("console") {

							{
								width("80%");
								lines(25);
								alignCenter();
								valignCenter();
								onStartScreenEffect(new EffectBuilder("move") {

									{
										length(150);
										inherit();
										neverStopRendering(true);
										effectParameter("mode", "in");
										effectParameter("direction", "top");
									}
								});
								onEndScreenEffect(new EffectBuilder("move") {

									{
										length(150);
										inherit();
										neverStopRendering(true);
										effectParameter("mode", "out");
										effectParameter("direction", "top");
									}
								});
							}
						});
					}
				});
			}
		}.registerPopup(nifty);
	}
}
