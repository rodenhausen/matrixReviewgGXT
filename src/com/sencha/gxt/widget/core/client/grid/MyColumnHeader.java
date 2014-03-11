package com.sencha.gxt.widget.core.client.grid;

import my.ex.client.TaxonMatrixView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.MyHorizontalAutoScrollSupport;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.fx.client.DragCancelEvent;
import com.sencha.gxt.fx.client.DragEndEvent;
import com.sencha.gxt.fx.client.DragHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.DragStartEvent;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.container.Container;

public class MyColumnHeader<M> extends ColumnHeader<M> {

	private Container scrollContainer;
	
	public MyColumnHeader(Widget container, ColumnModel<M> cm, Container scrollContainer) {
		super(container, cm, GWT
				.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class));
		this.scrollContainer = scrollContainer;
	}

	public MyColumnHeader(Widget container, ColumnModel<M> cm, Container scrollContainer,
			ColumnHeaderAppearance appearance) {
		super(container, cm, appearance);
		this.scrollContainer = scrollContainer;
	}

	/**
	 * True to enable column reordering.
	 * 
	 * @param enable
	 *            true to enable
	 */
	@Override
	public void setEnableColumnReorder(boolean enable) {
		this.enableColumnReorder = enable;

		if (enable && reorderer == null) {
			reorderer = new Draggable(this);
			reorderer.setUseProxy(true);
			reorderer.setSizeProxyToSource(false);
			reorderer.setProxy(StatusProxy.get().getElement());
			reorderer.setMoveAfterProxyDrag(false);

			reorderer.addDragHandler(new DragHandler() {
				private Head active;
				private int newIndex = -1;
				private Head start;
				private XElement statusIndicatorBottom;
				private XElement statusIndicatorTop;
				private StatusProxy statusProxy = StatusProxy.get();
				private MyHorizontalAutoScrollSupport scrollSupport;

				private Element adjustTargetElement(Element target) {
					return (Element) (target.getFirstChildElement() != null ? target
							.getFirstChildElement() : target);
				}

				private void afterDragEnd() {
					start = null;
					active = null;
					newIndex = -1;
					removeStatusIndicator();

					headerDisabled = false;

					if (bar != null) {
						bar.show();
					}

					quickTip.enable();
				}

				@SuppressWarnings("unchecked")
				private Head getHeadFromElement(Element element) {
					Widget head = ComponentHelper.getWidgetWithElement(element);
					Head h = null;
					if (head instanceof ColumnHeader.Head) {
						h = (Head) head;
					}
					return h;
				}

				@Override
				public void onDragCancel(DragCancelEvent event) {
					if(scrollSupport != null)
						scrollSupport.stop();
					afterDragEnd();
				}

				@Override
				public void onDragEnd(DragEndEvent event) {
					if(scrollSupport != null)
						scrollSupport.stop();
					
					if (statusProxy.getStatus()) {
						cm.moveColumn(start.column, newIndex);
					}
					afterDragEnd();
				}

				@Override
				public void onDragMove(DragMoveEvent event) {
					event.setX(event.getNativeEvent().getClientX() + 12
							+ XDOM.getBodyScrollLeft());
					event.setY(event.getNativeEvent().getClientY() + 12
							+ XDOM.getBodyScrollTop());

					Element target = event.getNativeEvent().getEventTarget()
							.cast();

					Head h = getHeadFromElement(adjustTargetElement(target));

					if (h != null && !h.equals(start)) {
						HeaderGroupConfig g = cm.getGroup(h.row - 1, h.column);
						HeaderGroupConfig s = cm.getGroup(start.row - 1,
								start.column);
						if ((g == null && s == null)
								|| (g != null && g.equals(s))) {
							active = h;
							boolean before = event.getNativeEvent()
									.getClientX() < active.getAbsoluteLeft()
									+ active.getOffsetWidth() / 2;
							showStatusIndicator(true);

							if (before) {
								statusIndicatorTop.alignTo(active.getElement(),
										new AnchorAlignment(Anchor.BOTTOM,
												Anchor.TOP_LEFT), new int[] {
												-1, 0 });
								statusIndicatorBottom.alignTo(active
										.getElement(), new AnchorAlignment(
										Anchor.TOP, Anchor.BOTTOM_LEFT),
										new int[] { -1, 0 });
							} else {
								statusIndicatorTop.alignTo(active.getElement(),
										new AnchorAlignment(Anchor.BOTTOM,
												Anchor.TOP_RIGHT), new int[] {
												1, 0 });
								statusIndicatorBottom.alignTo(active
										.getElement(), new AnchorAlignment(
										Anchor.TOP, Anchor.BOTTOM_RIGHT),
										new int[] { 1, 0 });
							}

							int i = active.column;
							if (!before) {
								i++;
							}

							int aIndex = i;

							if (start.column < active.column) {
								aIndex--;
							}
							newIndex = i;
							if (aIndex != start.column) {
								statusProxy.setStatus(true);
							} else {
								showStatusIndicator(false);
								statusProxy.setStatus(false);
							}
						} else {
							active = null;
							showStatusIndicator(false);
							statusProxy.setStatus(false);
						}

					} else {
						active = null;
						showStatusIndicator(false);
						statusProxy.setStatus(false);
					}
				}

				@Override
				public void onDragStart(DragStartEvent event) {
					System.out.println("drag start " + container);
					Grid grid = null;
					if (container instanceof Grid)
						grid = (Grid) container;

					if (grid != null) {
						if (scrollSupport == null) {
							scrollSupport = new MyHorizontalAutoScrollSupport(scrollContainer.getElement());
						} else if (scrollSupport.getScrollElement() == null) {
							scrollSupport.setScrollElement(grid.getView()
									.getScroller());
						}
						System.out.println("start scroll support");
						scrollSupport.start();
					}

					Element target = event.getNativeEvent().getEventTarget()
							.cast();

					Head h = getHeadFromElement(target);
					if (h != null && !h.config.isFixed()) {
						headerDisabled = true;
						quickTip.disable();
						if (bar != null) {
							bar.hide();
						}

						if (statusIndicatorBottom == null) {
							statusIndicatorBottom = XElement
									.createElement("div");
							statusIndicatorBottom.addClassName(styles
									.columnMoveBottom());
							statusIndicatorTop = XElement.createElement("div");
							statusIndicatorTop.addClassName(styles
									.columnMoveTop());
						}

						Document.get().getBody()
								.appendChild(statusIndicatorTop);
						Document.get().getBody()
								.appendChild(statusIndicatorBottom);

						start = h;
						statusProxy.setStatus(false);
						statusProxy.update(start.config.getHeader());
					} else {
						event.setCancelled(true);
					}
				}

				private void removeStatusIndicator() {
					if (statusIndicatorBottom != null) {
						statusIndicatorBottom.removeFromParent();
						statusIndicatorTop.removeFromParent();
					}
				}

				private void showStatusIndicator(boolean show) {
					if (statusIndicatorBottom != null) {
						statusIndicatorBottom.setVisibility(show);
						statusIndicatorTop.setVisibility(show);
					}
				}
			});
		}

		if (reorderer != null && !enable) {
			reorderer.release();
			reorderer = null;
		}
	}

}
