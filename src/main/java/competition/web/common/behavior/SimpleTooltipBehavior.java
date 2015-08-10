package competition.web.common.behavior;

/**
 * @author Decebal Suiu
 */
public class SimpleTooltipBehavior extends AbstractTooltipBehavior {

	private String tooltip;

	public SimpleTooltipBehavior(String tooltip) {
		this.tooltip = tooltip;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

}
