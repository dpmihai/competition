package competition.web.common.table;

import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;


public class BaseTable<T, S> extends AjaxFallbackDefaultDataTable<T, S> {

	private static final long serialVersionUID = 1L;

	public BaseTable(String id, List<IColumn<T, S>> columns, ISortableDataProvider<T, S> dataProvider, int rowsPerPage) {
		super(id, columns, dataProvider, rowsPerPage);
	}

}
