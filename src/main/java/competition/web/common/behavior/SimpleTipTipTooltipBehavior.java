package competition.web.common.behavior;

/**
 * @author Decebal Suiu
 */
public class SimpleTipTipTooltipBehavior extends AbstractTipTipTooltipBehavior {

    private String tooltip;

    public SimpleTipTipTooltipBehavior(String tooltip) {
	this.tooltip = tooltip;
    }

    @Override
    public String getTooltip() {
	return tooltip;
    }

}
