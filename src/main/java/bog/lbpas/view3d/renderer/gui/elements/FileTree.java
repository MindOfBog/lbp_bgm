package bog.lbpas.view3d.renderer.gui.elements;

import bog.lbpas.view3d.core.Model;
import bog.lbpas.view3d.core.Texture;
import bog.lbpas.view3d.mainWindow.ConstantTextures;
import bog.lbpas.view3d.managers.MouseInput;
import bog.lbpas.view3d.managers.RenderMan;
import bog.lbpas.view3d.managers.WindowMan;
import bog.lbpas.view3d.managers.assetLoading.ObjectLoader;
import bog.lbpas.view3d.renderer.gui.cursor.ECursor;
import bog.lbpas.view3d.renderer.gui.ingredients.LineStrip;
import bog.lbpas.view3d.utils.Config;
import bog.lbpas.view3d.utils.Consts;
import bog.lbpas.view3d.utils.Cursors;
import bog.lbpas.view3d.utils.print;
import org.joml.Math;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public abstract class FileTree extends Element{

    public TreeFolder root;
    public int fontSize;
    public float itemHeight;

    public FileTree(String id, String rootName, float itemHeight, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        super();
        this.id = id;
        this.pos = pos;
        this.size = size;
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.itemHeight = itemHeight;

        init(rootName);
    }

    public FileTree(String id, String rootName, float itemHeight, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
        super();
        this.id = id;
        this.pos = new Vector2f();
        this.size = new Vector2f();
        this.fontSize = fontSize;
        this.renderer = renderer;
        this.loader = loader;
        this.window = window;
        this.itemHeight = itemHeight;

        init(rootName);
    }

    private void init(String rootName)
    {
        root = new TreeFolder("root", null, rootName, true, false, new Vector2f(), new Vector2f(this.size.x, itemHeight), fontSize, renderer, loader, window) {

            @Override
            public Texture getIcon() {
                return getRootIcon(this.itemName.getText(), extended, this.size);
            }
        };
    }

    public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedElement)
    {
        super.onClick(mouseInput, pos, button, action, mods, overElement, focusedElement);

        root.onClick(mouseInput, pos, button, action, mods, overElement, focusedElement);
    }
    public void onKey(int key, int scancode, int action, int mods)
    {
        super.onKey(key, scancode, action, mods);

        root.onKey(key, scancode, action, mods);
    }
    public void onChar(int codePoint, int modifiers)
    {
        super.onChar(codePoint, modifiers);

        root.onChar(codePoint, modifiers);
    }
    public void onMouseScroll(Vector2d pos, double xOffset, double yOffset)
    {
        super.onMouseScroll(pos, xOffset, yOffset);

        root.onMouseScroll(pos, xOffset, yOffset);
    }
    public void onMouseMove(MouseInput mouseInput, double x, double y, boolean overElement)
    {
        super.onMouseMove(mouseInput, x, y, overElement);

        root.onMouseMove(mouseInput, x, y, overElement);
    }
    public void draw(MouseInput mouseInput, boolean overElement)
    {
        super.draw(mouseInput, overElement);
        root.pos.x = this.pos.x;
        root.pos.y = this.pos.y;
        root.size.x = this.size.x;
        root.draw(mouseInput, overElement);
        this.size.y = root.getFullHeight();
    }
    public void resize()
    {
        super.resize();
        root.resize();
    }

    public void secondThread()
    {
        super.secondThread();
        root.secondThread();
    }

    @Override
    public boolean isFocused() {
        return super.isFocused() || root.isFocused();
    }

    public Texture getItemIcon(String itemName, Vector2f size)
    {
        int s = Math.round(size.y);
        return ConstantTextures.getTexture(ConstantTextures.FILE, s, s, loader);
    }
    public Texture getFolderIcon(String itemName, boolean extended, Vector2f size)
    {
        int s = Math.round(size.y);
        return ConstantTextures.getTexture(extended ? ConstantTextures.ICON_GROUP_OPEN : ConstantTextures.ICON_GROUP, s, s, loader);
    }
    public Texture getRootIcon(String itemName, boolean extended, Vector2f size)
    {
        int s = Math.round(size.y);
        return ConstantTextures.getTexture(extended ? ConstantTextures.ICON_GROUP_OPEN : ConstantTextures.ICON_GROUP, s, s, loader);
    }

    public abstract int[] getParentTransform();

    @Override
    public boolean isMouseOverElement(Vector2f mousePos) {
        return super.isMouseOverElement(mousePos) || root.isMouseOverElement(mousePos);
    }

    public TreeItem getSelectedItem()
    {
        if(root.selected)
            return root;

        return getChildrenSelected(root);
    }
    private TreeItem getChildrenSelected(TreeFolder folder)
    {
        for(TreeItem child : folder.children)
        {
            if(child.selected)
                return child;
            if(child instanceof TreeFolder)
            {
                TreeItem item = getChildrenSelected((TreeFolder) child);
                if(item != null)
                    return item;
            }
        }

        return null;
    }

    public String getPath(TreeItem treeItem)
    {
        if(treeItem.parent == root)
            return treeItem.itemName.getText();
        else if(treeItem.parent == null || treeItem == root)
            return "";

        String path = getPath(treeItem.parent);
        return ((path == null || path.isEmpty() || path.isBlank()) ? "" : path + "/") + treeItem.itemName.getText();
    }

    public abstract void rename(TreeItem treeItem);
    public abstract void delete(TreeItem treeItem);
    public abstract void copy(TreeItem treeItem);
    public abstract void replace(TreeItem treeItem);
    public abstract void paste(TreeFolder treeFolder);
    public abstract void newFolder(TreeFolder treeFolder);
    public void addOptionsItem(ComboBox comboBox, TreeItem treeItem)
    {
        comboBox.addButton("Copy", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    copy(treeItem);
            }
        });
        comboBox.addButton("Cut", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                {
                    copy(treeItem);
                    delete(treeItem);
                }
            }
        });
        comboBox.addButton("Replace", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    replace(treeItem);
            }
        });
    }
    public void addOptionsFolder(ComboBox comboBox, TreeFolder folder)
    {
        comboBox.addButton("New Folder", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
                    newFolder(folder);
            }
        });
        comboBox.addButton("Paste", new Button() {
            @Override
            public void clickedButton(int button, int action, int mods) {
                if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS)
                    paste(folder);
            }
        });
    }


    public class TreeItem extends Panel
    {
        public TreeFolder parent;

        public Textbox itemName;
        public ButtonImage iconButton;
        public ButtonImage deleteButton;
        public ButtonImage renameButton;
        public ComboBoxImage optionsCombo;
        PanelElement icon;
        PanelElement delete;
        PanelElement rename;
        PanelElement options;
        PanelElement gap;
        PanelElement name;

        public boolean reNameable = true;
        public boolean selected = false;

        public Object item;

        private boolean hasRename = true;
        private boolean hasDelete = true;

        public TreeItem(String id, Object item, String name, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            super(pos, size, renderer);
            this.renderer = renderer;
            this.loader = loader;
            this.window = window;
            this.item = item;
            setupElements(name, fontSize);
        }

        public TreeItem(String id, Object item, String name, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            super(new Vector2f(), new Vector2f(), renderer);
            this.renderer = renderer;
            this.loader = loader;
            this.window = window;
            this.item = item;
            setupElements(name, fontSize);
        }

        public TreeItem(String id, Object item, String name, boolean hasRename, boolean hasDelete, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            super(pos, size, renderer);
            this.renderer = renderer;
            this.loader = loader;
            this.window = window;
            this.item = item;
            this.hasRename = hasRename;
            this.hasDelete = hasDelete;
            setupElements(name, fontSize);
        }

        public TreeItem(String id, Object item, String name, boolean hasRename, boolean hasDelete, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            super(new Vector2f(), new Vector2f(), renderer);
            this.renderer = renderer;
            this.loader = loader;
            this.window = window;
            this.item = item;
            this.hasRename = hasRename;
            this.hasDelete = hasDelete;
            setupElements(name, fontSize);
        }

        private void setupElements(String name, int fontSize)
        {
            iconButton = getIconButton();
            if(hasRename)
                renameButton = new ButtonImage("renameButton", renderer, loader, window) {
                    @Override
                    public void clickedButton(int button, int action, int mods) {
                        if(action == GLFW.GLFW_PRESS)
                            itemName.setFocused(true);
                    }

                    @Override
                    public Texture getImage() {
                        int s = Math.round(this.size.y);
                        return ConstantTextures.getTexture(ConstantTextures.RENAME, s, s, loader);
                    }
                };
            optionsCombo = new ComboBoxImage("optionsCombo", fontSize, 200, renderer, loader, window) {
                @Override
                public Texture getImage() {
                    int size = Math.round(this.size.y);
                    return ConstantTextures.getTexture(ConstantTextures.OPTIONS, size, size, loader);
                }

                @Override
                public int[] getParentTransform() {
                    return FileTree.this.getParentTransform();
                }
            };

            addOptions(optionsCombo);

            if(hasDelete){
                TreeItem object = this;
                deleteButton = new ButtonImage("delete", renderer, loader, window) {
                    @Override
                    public void clickedButton(int button, int action, int mods) {
                        if(action == GLFW.GLFW_PRESS)
                            delete(object);
                    }

                    @Override
                    public Texture getImage() {
                        int s = Math.round(this.size.y);
                        return ConstantTextures.getTexture(ConstantTextures.WINDOW_CLOSE, s, s, loader);
                    }
                };
            }
            itemName = new Textbox("itemName", fontSize, renderer, loader, window)
            {
                @Override
                public void draw(MouseInput mouseInput, boolean overOther) {
                    hovering = isMouseOverElement(mouseInput) && !overOther;
                    if(hovering)
                        hoverCursor();

                    if(prevSize == null || prevSize.x != size.x || prevSize.y != size.y)
                    {
                        resize();
                        prevSize = new Vector2f(size);
                    }

                    renderer.startScissor((int) Math.round(pos.x), (int) Math.round(pos.y), (int) Math.round(size.x), (int) Math.round(size.y));

//            if(isMouseOverElement(mouseInput) || isFocused())
                    renderer.drawRect(Math.round(pos.x), Math.round(pos.y), Math.round(size.x), Math.round(size.y), isMouseOverElement(mouseInput) && !overOther || this.isFocused() || selected ? Config.INTERFACE_SECONDARY_COLOR : Config.INTERFACE_PRIMARY_COLOR);

                    if(!this.isFocused())
                    {
                        currentSelection = text.length();
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                    }

                    float xScroll = getXScroll(mouseInput);

                    int begin = 0;
                    int end = text.length();

                    for(int i = 0; i < text.length(); i++)
                    {
                        try
                        {
                            if((int) Math.round(pos.x + xScroll + this.size.y / 2 - getFontHeight(fontSize) / 2) + getStringWidth(text.substring(0, i), fontSize) < pos.x)
                                begin = i;

                            if((int) Math.round(pos.x + xScroll + this.size.y / 2 - getFontHeight(fontSize) / 2) + getStringWidth(text.substring(0, i + 1), fontSize) < pos.x + size.x)
                                end = i + 1;
                        }catch (Exception e){}
                    }

                    renderer.drawString(text, textColor(), (int) Math.round(pos.x + xScroll + this.size.y / 2 - getFontHeight(fontSize) / 2), (int) Math.round(pos.y + this.size.y / 2 - getFontHeight(fontSize) / 2), fontSize, begin, end);

                    if(!this.disabled)
                    {
                        if(500 > (System.currentTimeMillis() - Consts.startMillis) % 1000 && isFocused())
                            if(currentSelection == text.length())
                                renderer.drawString("_", textColor(), (int) Math.round(pos.x + xScroll + getStringWidth(text, fontSize) + 1 + this.size.y/2 - getFontHeight(fontSize)/2), (int) Math.round(pos.y + this.size.y/2 - getFontHeight(fontSize)/2), fontSize);
                            else
                                renderer.drawRect((int) Math.round(xScroll + pos.x + getStringWidth(text.substring(0, currentSelection), fontSize) + this.size.y/2 - getFontHeight(fontSize)/2 - 1), (int) Math.round(pos.y + this.size.y/2 - getFontHeight(fontSize)/2), 1, (int) Math.round(getFontHeight(fontSize) - 2), textColor());

                        if(selectedText[0] >= 0 && selectedText[1] >= 0 && selectedText[0] != selectedText[1])
                        {
                            int selectionStart = selectedText[0];
                            int selectionEnd = selectedText[1];

                            if(selectionStart > selectionEnd)
                            {
                                selectionStart = selectedText[1];
                                selectionEnd = selectedText[0];
                            }

                            float[] pos1 = {
                                    pos.x + xScroll + this.size.y / 2 - getFontHeight(fontSize) / 2,
                                    pos.y + this.size.y / 2 - getFontHeight(fontSize) / 2
                            };

                            float x = pos1[0] + getStringWidth(text.substring(0, selectionStart), fontSize);
                            float diff = 0;
                            if (x < pos.x) {
                                diff = x - pos.x;
                                x = pos.x;
                            }

                            float width = getStringWidth(text.substring(selectionStart, selectionEnd), fontSize) + 1 + diff;
                            if (x + width > pos.x + size.x)
                                width = pos.x + size.x - x;

                            renderer.drawRectInvert((int) Math.round(x - 1), (int) Math.round(pos1[1] - 1), (int) Math.round(width + 1), getFontHeight(fontSize) + 2);
                        }
                    }
                    renderer.drawRectOutline(new Vector2f((int) Math.round(pos.x), (int) Math.round(pos.y)), outlineRect, isMouseOverElement(mouseInput) && !overOther || this.isFocused() ? Config.INTERFACE_SECONDARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2, false);
                    renderer.endScissor();
                }

                long lastClicked = 0;

                @Override
                public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overOther, boolean focusedOther) {

                    if(isMouseOverElement(pos))
                    {
                        if(button == GLFW.GLFW_MOUSE_BUTTON_1 && !overOther)
                        {
                            if(action == GLFW.GLFW_PRESS)
                            {
                                int cpos = getCursorPos(mouseInput);

                                if(System.currentTimeMillis() - mouseInput.lastLeftDownMS <= 500 && currentSelection == cpos && this.isFocused())
                                {
                                    mouseInput.leftButtonPress = false;
                                    for(int i = cpos; i >= 0; i--)
                                        if(i - 1 <= 0)
                                        {
                                            selectedText[0] = i - 1 < 0 ? 0 : i - 1;
                                            break;
                                        }
                                        else if(text.substring(i - 1, i).equalsIgnoreCase(" "))
                                        {
                                            selectedText[0] = i;
                                            break;
                                        }
                                    for(int i = cpos; i <= text.length(); i++)
                                        if(i + 1 >= text.length())
                                        {
                                            selectedText[1] = i + 1 > text.length() ? text.length() : i + 1;
                                            currentSelection = selectedText[1];
                                            break;
                                        }
                                        else if(text.substring(i, i + 1).equalsIgnoreCase(" "))
                                        {
                                            selectedText[1] = i;
                                            currentSelection = i;
                                            break;
                                        }
                                }
                                else if(reNameable)
                                {
                                    if(System.currentTimeMillis() - lastClicked <= 500)
                                        this.setFocused(true);
                                    lastClicked = System.currentTimeMillis();

                                    currentSelection = cpos;
                                    selectedText[0] = currentSelection;
                                    selectedText[1] = currentSelection;
                                }
                            }
                        }
                    }
                    else
                    {
                        if(action == GLFW.GLFW_PRESS)
                            this.setFocused(false);
                    }

                    if(button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE && selectedText[0] == selectedText[1])
                    {
                        selectedText[0] = -1;
                        selectedText[1] = -1;
                    }
                }

                @Override
                public void hoverCursor() {
                    if(isFocused())
                        super.hoverCursor();
                    else
                        Cursors.setCursor(ECursor.hand2);
                }

                @Override
                public void onKey(int key, int scancode, int action, int mods) {
                    if(key == GLFW.GLFW_KEY_ENTER && action == GLFW.GLFW_PRESS)
                        this.setFocused(false);

                    super.onKey(key, scancode, action, mods);
                }
            };
            itemName.setText(name);

            this.icon = new PanelElement(this.iconButton, 10);
            this.elements.add(this.icon);
            this.gap = new PanelElement(null, 0);
            this.elements.add(this.gap);
            this.name = new PanelElement(this.itemName, 0);
            this.elements.add(this.name);
            if(hasRename){
                this.elements.add(this.gap);
                this.rename = new PanelElement(this.renameButton, 0);
                this.elements.add(this.rename);
            }
            this.elements.add(this.gap);
            this.options = new PanelElement(this.optionsCombo, 0);
            this.elements.add(this.options);
            if(hasDelete) {
                this.elements.add(this.gap);
                this.delete = new PanelElement(this.deleteButton, 0);
                this.elements.add(this.delete);
            }
        }

        @Override
        public void resize() {
            float button = this.size.y / this.size.x;
            float gap = 2.25f / this.size.x;
            float rest = 1f - (button + gap) * (2 + (hasRename ? 1 : 0) + (hasDelete ? 1 : 0));

            this.icon.width = button;
            this.gap.width = gap;
            this.name.width = rest;
            if(hasDelete) this.delete.width = button;
            if(hasRename) this.rename.width = button;
            this.options.width = button;

            super.resize();
        }

        public Texture getIcon()
        {
            return getItemIcon(this.itemName.getText(), this.size);
        }

        @Override
        public boolean isFocused() {
            return super.isFocused() || itemName.isFocused();
        }

        @Override
        public void secondThread() {
            if (itemName.isFocused())
                try
                {
                    rename(this);
                }catch(Exception e){print.stackTrace(e);}

            super.secondThread();
        }

        public ButtonImage getIconButton()
        {
            return new ButtonImage("icon", renderer, loader, window) {
                @Override
                public void clickedButton(int button, int action, int mods) {
                    if(action == GLFW.GLFW_PRESS)
                        selected = !selected;
                }

                @Override
                public Texture getImage() {
                    return getIcon();
                }
            };
        }

        @Override
        public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedOther) {
            super.onClick(mouseInput, pos, button, action, mods, overElement, focusedOther);

            if((isMouseOverPanel(mouseInput) || this.optionsCombo.isMouseOverElement(pos)) && !overElement)
            {
                if(!this.iconButton.isMouseOverElement(pos) &&
                        (this.deleteButton == null ? true : !this.deleteButton.isMouseOverElement(pos)) &&
                        !this.optionsCombo.isMouseOverElement(pos))
                {
                    if(action == GLFW.GLFW_PRESS && button != GLFW.GLFW_MOUSE_BUTTON_2)
                        selected = !selected;
                    else if(button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_RELEASE)
                    {
                        optionsCombo.extended = true;
                        selected = true;
                    }
                }
                else if(this.optionsCombo.isMouseOverTab(pos) && !this.optionsCombo.extended)
                {
                    optionsCombo.extended = true;
                    selected = true;
                }
            }
            else
                if(action == GLFW.GLFW_PRESS)
                    selected = false;
        }

        @Override
        public void onKey(int key, int scancode, int action, int mods) {
            super.onKey(key, scancode, action, mods);

            if(key == GLFW.GLFW_KEY_DELETE && action == GLFW.GLFW_PRESS && selected)
                delete(this);
        }

        protected void addOptions(ComboBoxImage optionsCombo) {
            addOptionsItem(optionsCombo, this);
        }
    }

    public class TreeFolder extends TreeItem
    {
        public ArrayList<TreeItem> children;
        boolean extended = true;

        private Model treeLines;

        public TreeFolder(String id, Object item, String name, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            super(id, item, name, pos, size, fontSize, renderer, loader, window);
            this.children = new ArrayList<>();
        }

        public TreeFolder(String id, Object item, String name, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            super(id, item, name, fontSize, renderer, loader, window);
            this.children = new ArrayList<>();
        }

        public TreeFolder(String id, Object item, String name, boolean hasRename, boolean hasDelete, Vector2f pos, Vector2f size, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            super(id, item, name, hasRename, hasDelete, pos, size, fontSize, renderer, loader, window);
            this.children = new ArrayList<>();
        }

        public TreeFolder(String id, Object item, String name, boolean hasRename, boolean hasDelete, int fontSize, RenderMan renderer, ObjectLoader loader, WindowMan window) {
            super(id, item, name, hasRename, hasDelete, fontSize, renderer, loader, window);
            this.children = new ArrayList<>();
        }

        public void onClick(MouseInput mouseInput, Vector2d pos, int button, int action, int mods, boolean overElement, boolean focusedElement)
        {
            super.onClick(mouseInput, pos, button, action, mods, overElement, focusedElement);

            if(extended)
                for(int i = 0; i < children.size(); i++)
                    children.get(i).onClick(mouseInput, pos, button, action, mods, overElement, focusedElement);
        }
        public void onKey(int key, int scancode, int action, int mods)
        {
            super.onKey(key, scancode, action, mods);

            if(extended)
                for(TreeItem child : children)
                    child.onKey(key, scancode, action, mods);
        }
        public void onChar(int codePoint, int modifiers)
        {
            super.onChar(codePoint, modifiers);

            if(extended)
                for(TreeItem child : children)
                    child.onChar(codePoint, modifiers);
        }
        public void onMouseScroll(Vector2d pos, double xOffset, double yOffset)
        {
            super.onMouseScroll(pos, xOffset, yOffset);

            if(extended)
                for(TreeItem child : children)
                    child.onMouseScroll(pos, xOffset, yOffset);
        }
        public void onMouseMove(MouseInput mouseInput, double x, double y, boolean overElement)
        {
            super.onMouseMove(mouseInput, x, y, overElement);

            if(extended)
                for(TreeItem child : children)
                    child.onMouseMove(mouseInput, x, y, overElement);
        }
        public void draw(MouseInput mouseInput, boolean overElement)
        {
            super.draw(mouseInput, overElement);

            float x = this.pos.x;
            float y = this.pos.y + this.size.y;

            if(extended)
            {
                for(TreeItem child : children)
                {
                    y = Math.round(y);
                    int nx = Math.round(this.size.y + 2);
                    child.pos.x = x + nx;
                    child.pos.y = 2 + y;
                    child.size.x = this.size.x - nx;
                    y += (child instanceof TreeFolder ? ((TreeFolder)child).getFullHeight() : child.size.y) + 2;
                    child.draw(mouseInput, overElement);
                }
                renderer.drawLineStrip(this.treeLines, this.pos, areChildrenSelected() ? Config.INTERFACE_TERTIARY_COLOR2 : Config.INTERFACE_PRIMARY_COLOR2, false);
            }
        }
        public void resize()
        {
            super.resize();

            if(this.treeLines != null)
                this.treeLines.cleanup(loader);

            Vector2f[] points = new Vector2f[1 + (3 * children.size())];

            float x = Math.round(this.size.y / 2 - 0.5f);
            points[0] = new Vector2f(x, this.size.y * 1.25f - 0.5f);

            for(int i = 0; i < children.size(); i++)
            {
                TreeItem child = children.get(i);
                points[1 + (i * 3)] = new Vector2f(x, child.pos.y - this.pos.y + child.size.y / 2f - 0.5f);
                points[2 + (i * 3)] = new Vector2f(child.pos.x - this.pos.x - (x / 2f) - 0.5f, child.pos.y - this.pos.y + child.size.y / 2f - 0.5f);
                points[3 + (i * 3)] = new Vector2f(x, child.pos.y - this.pos.y + child.size.y / 2f - 0.5f);
            }

            this.treeLines = LineStrip.processVerts(points, loader, window);

            for(int i = 0; i < children.size(); i++)
                children.get(i).resize();
        }

        public void secondThread()
        {
            super.secondThread();

            if(extended)
                for(int i = 0; i < children.size(); i++)
                    children.get(i).secondThread();
        }

        @Override
        public Texture getIcon() {
            return getFolderIcon(this.itemName.getText(), extended, this.size);
        }

        @Override
        public boolean isFocused() {
            boolean isChildFocused = false;

            for(TreeItem child : children)
                if(child.isFocused())
                    isChildFocused = true;

            return super.isFocused() || isChildFocused;
        }

        @Override
        public ButtonImage getIconButton()
        {
            return new ButtonImage("icon", renderer, loader, window) {
                @Override
                public void clickedButton(int button, int action, int mods) {
                    if(action == GLFW.GLFW_PRESS)
                        extended = !extended;
                }

                @Override
                public Texture getImage() {
                    return getIcon();
                }
            };
        }

        @Override
        protected void addOptions(ComboBoxImage optionsCombo) {
            addOptionsFolder(optionsCombo, this);
        }

        public boolean areChildrenSelected()
        {
            boolean selected = false;

            for(int i = 0; i < children.size(); i++)
                if(children.get(i).selected || children.get(i).isFocused())
                    selected = true;

            return selected;
        }

        public int getFullHeight()
        {
            float height = this.size.y;

            if(extended)
                for(int i = 0; i < children.size(); i++)
                {
                    TreeItem child = children.get(i);
                    height += 2 + (child instanceof TreeFolder ? ((TreeFolder)child).getFullHeight() : child.size.y);
                    height = Math.round(height);
                }

            return Math.round(height);
        }

        public TreeItem addItem(String id, Object item, String name, float height)
        {
            TreeItem entry = new TreeItem(id, item, name, new Vector2f(), new Vector2f(0, height), fontSize, renderer, loader, window);
            entry.parent = this;
            this.children.add(entry);
            return entry;
        }

        public TreeFolder addFolder(String id, Object item, String name, float height)
        {
            TreeFolder folder = new TreeFolder(id, item, name, new Vector2f(), new Vector2f(0, height), fontSize, renderer, loader, window);
            folder.parent = this;
            this.children.add(folder);
            return folder;
        }

        @Override
        public boolean isMouseOverElement(Vector2f mousePos) {
            boolean isOver = super.isMouseOverElement(mousePos);

            for(int i = 0; i < children.size(); i++)
                if(children.get(i).isMouseOverElement(mousePos))
                    isOver = true;

            return isOver;
        }
    }
}
