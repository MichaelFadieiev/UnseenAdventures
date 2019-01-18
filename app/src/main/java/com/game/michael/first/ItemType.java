package com.game.michael.first;

import android.app.Activity;

import java.util.ArrayList;

import static com.game.michael.first.SourceJSON.*;

/*
* Общее:
* Предметы в игре имеют название, физичекие характеристики (вес, объём), базовую ценность, не имеет
* владельца, однозначно определяются идентификатором. Предметы делятся на группы, что определяет
* возможные действия с ними и особенности. У всех предметов есть время жизни, по оконччании которого
* предмет разрушается. Предметы могут находится в инвентаре, в локации, на территории или быть в
* одном из соответствующих слотов экипировки. По сути предмет - это стак (коллекция) времени жизни
* предметов одного типа, причём стак отсортирован по уменьшению времени жизни.
*
* Переменные:
* public int itemID             уникальный идентификатор предмета
*            itemUID            идентификатор уникального предмета, контейнеры, емкости, артефакты.
*                               Если -1 - не уникальный предмет (можно складывать в стаки),
*                               0 - контейнер или емкость, >0 - артефакт
* public String name            название предмета (удалено, вытягивается только для UI)
* public int volume             объем 1 ед
*            weight             вес 1 ед
*            cost               базовая ценность 1 ед
* public ArrayList<int[]> endDT стак времени жизни. Хранит времена разрушения предметов в виде
*                               массива {год; месяц; день; час; минута}
*
* Конструкторы:
* public ItemType ()
*   - создание пустого стака предметов исходного типа
* public ItemType (Activity p_act, int p_itemID, int[] p_endDT)
*   -
* public ItemType (String p_name, int[] p_date)
*   -
*
* Функции:
* public ItemType cloneEmpty (ItemType p_item)
*   - копирование пустого стака предметов исходного типа
*
* Процедуры:
* public void brokeItem (int[] p_dateTime)
*   - удаление/разрушение всех предметов в стаке, срок жизни которых прошел.
* */
class ItemType {

    int itemID;
    int itemUID; //
    //public String name;
    int volume, weight, cost; //базовые величины, для 1 шт
    ArrayList<int[]> endDT;

    boolean isLiquid;
    boolean isContainer;
    boolean isJar;
    String[] tags;
    ArrayList<ItemType> contain;
    int volumeFree;
    int[] damage;
    int ownerID;

    ItemType () {
        endDT = new ArrayList<>();
        damage = new int[5];
        //name = new String("");
    }

    ItemType (GameActivity p_act, int p_itemID, int p_itemUID, int p_contain, int p_quantity) {
        //volume = getItemInt(p_act, p_itemID, "volume");
        //weight = getItemInt(p_act, p_itemID, "weight");
        //cost = getItemInt(p_act, p_itemID, "cost");

        //v_tags[0].equals("drnk") || v_tags[2].equals("drnk"));
        itemID = p_itemID;
        itemUID = p_itemUID;
        endDT = new ArrayList<>(1);
        damage = new int[5];
        /*for (int i=0; i<p_quantity; i++) {
            i
            endDT.add(GameActivity.flowTime(p_act.dateTime, SourceJSON.getItemInt(p_act, p_itemID, "lifetime")));
        }*/
        tags = new String[4];
        loadItem(p_act, this, p_quantity);
        isJar = (tags[0].equals("jar") || tags[2].equals("jar"));
        isContainer = ((tags[0].equals("cntr") || tags[2].equals("cntr")) && !(isJar));
        if (isJar || isContainer) {
            contain = new ArrayList<>(1);
            volumeFree = Math.round(volume * Constants.itemVolumeFreeCoefficient);
        }
        if (isJar && (p_contain>=0)) {
            this.insertItem(new ItemType(p_act, p_contain, -1, -1, volumeFree/100));
        }
        if (isContainer && (p_contain>=0)) {
            loadSet(p_act, this.contain, p_contain);
            while (this.endDT.size()>1) this.endDT.remove(this.endDT.size()-1);
        }

        ownerID = -1;
    }

    //Создание магического камушка. For TEST purposes.
    /*public ItemType (int p_ID, int[] p_date) {
        endDT = new ArrayList<>(1);
        itemID = p_ID;

        //name = p_name;
        volume = 300;
        weight = 500;
        cost = 1000;
        endDT.add(GameActivity.flowTime(p_date, 1000));
    }*/
/*
    public ItemType createItem (int p_itemID, int[] p_createdDT) {
        return new ItemType(p_itemID, p_createdDT);
    }
*/
    static ItemType cloneEmpty (ItemType p_item) {
        ItemType v_item = new ItemType();
        v_item.itemID   = p_item.itemID;
        v_item.itemUID  = p_item.itemUID;
        v_item.volume   = p_item.volume;
        v_item.weight   = p_item.weight;
        v_item.cost     = p_item.cost;
        v_item.isJar    = p_item.isJar;
        v_item.isContainer = p_item.isContainer;
        v_item.isLiquid = p_item.isLiquid;
        v_item.tags = p_item.tags.clone();
        if (v_item.isJar || v_item.isContainer) {
            v_item.contain = new ArrayList<>(1);
            v_item.volumeFree = Math.round(v_item.volume * Constants.itemVolumeFreeCoefficient);
        }
        return v_item;
    }

    //Заполнение сосудов
    /*boolean fillJar (ItemType p_item) {
        //расчет количества ед, которые влезут
        //System.out.println("input_size" + String.valueOf(p_item.endDT.size()));
        //System.out.println("calculated_size" + String.valueOf(this.volumeFree / p_item.volume));
        int v_size = Math.min(p_item.endDT.size(), (this.volumeFree / p_item.volume));
        //System.out.println(String.valueOf(v_size));
        if (v_size == 0) {return false;}
        if (!this.contain.isEmpty()) {
            if(this.contain.get(0).addItem(p_item.takeItem(v_size).endDT)) {
                this.volumeFree -= v_size * p_item.volume;
                this.weight += v_size * p_item.weight;
                return true;
            }
        } else {
            if(contain.add(p_item.takeItem(v_size))) {
                this.volumeFree -= v_size * p_item.volume;
                this.weight += v_size * p_item.weight;
                //System.out.println("Filled empty");
                return true;
            }
            //System.out.println("Not filled empty");
        }
        //System.out.println("Not worked at all");
        return false;
    }*/

    //Добавление стака предметов к стаку (доступ извне)
    void addItem (ArrayList<int[]> p_endDT) {
        //int v_oldSize = this.endDT.size();
        while (p_endDT.size() > 0) {this.addItem(p_endDT.remove(p_endDT.size()-1));}
        //return (this.endDT.size() > v_oldSize);
    }

    //Добавление предмета к стаку (сохраняя сортировку)
    private void addItem (int[] p_endDT) {
        int v_oldSize = this.endDT.size();           //запоминаем изначальный размер
        for (int i=0; i < v_oldSize; i++) {     //перебор элементов по списку
            if ((endDT.get(i)[0] <= p_endDT[0])&&
                    (endDT.get(i)[1] <= p_endDT[1])&&
                    (endDT.get(i)[2] <= p_endDT[2])&&
                    (endDT.get(i)[3] <= p_endDT[3])&&
                    (endDT.get(i)[4] <= p_endDT[4])) {       //поиск элемента с меньшим конечным сроком
                endDT.add(i, p_endDT);
                endDT.trimToSize();
                return;}
        }
        endDT.add(p_endDT);                     //если все даты больше чем добавляемая
        endDT.trimToSize();
        //return (endDT.size()>v_oldSize);
    }

    //filling container or jar. Return true if any amount of items was successfully placed
    boolean insertItem(ItemType p_item) {
        int oldSizeTMP = p_item.endDT.size();
        if (isContainer) { //filling container
            ItemType v_item;
            if ( ! p_item.isLiquid) {
                v_item = p_item.takeItem(Math.min(p_item.endDT.size(), this.volumeFree / p_item.volume));
                if (!v_item.endDT.isEmpty()) {
                    int v_indThere = -1;
                    ItemType itemTMP;
                    for (int i = 0; i < this.contain.size(); i++) {
                        itemTMP = this.contain.get(i);
                        if ((v_item.itemID == itemTMP.itemID) && (v_item.itemUID < 0)
                                && (itemTMP.itemUID < 0) && (!itemTMP.isJar) && (!itemTMP.isContainer)) {
                            v_indThere = i;
                            break;
                        }
                    }
                    if (v_indThere >= 0) {
                        this.contain.get(v_indThere).addItem(v_item.endDT);
                    } else {
                        this.contain.add(v_item);
                    }
                    this.volumeFree -= v_item.endDT.size() * v_item.volume;
                    this.weight += v_item.endDT.size() * v_item.weight;
                }
            } else {
                for (int i = 0; i < this.contain.size(); i++) {
                    v_item = this.contain.get(i);
                    if (v_item.isJar) {
                        if ((v_item.contain.isEmpty()) || (v_item.contain.get(0).itemID == p_item.itemID && v_item.volumeFree>=p_item.volume)) {
                            int oldSize = p_item.endDT.size();
                            if (v_item.insertItem(p_item)) {
                                this.weight += v_item.weight * (oldSize - p_item.endDT.size());
                            }
                        }
                        if (p_item.endDT.isEmpty()) return true;
                    }
                }
            }
        } else if (this.isJar && p_item.isLiquid) { //filling jar
            int v_size = Math.min(p_item.endDT.size(), (this.volumeFree / p_item.volume));
            if (v_size == 0) {return false;}
            if ( ! this.contain.isEmpty()) {
                this.contain.get(0).addItem(p_item.takeItem(v_size).endDT);
                this.volumeFree -= v_size * p_item.volume;
                this.weight += v_size * p_item.weight;
                //return true;

            } else if (this.contain.add(p_item.takeItem(v_size))) {
                this.volumeFree -= v_size * p_item.volume;
                this.weight += v_size * p_item.weight;
                //return true;
            }
        } else return false;
        return (oldSizeTMP != p_item.endDT.size());
    }

    //
    ItemType extractItem(int p_index, int p_quantity) {
        if (this.isContainer || this.isJar) {
            if ( ! this.contain.isEmpty()) {
                ItemType v_item = this.contain.get(p_index).takeItem(p_quantity);
                this.weight -= v_item.weight * v_item.endDT.size();
                this.volumeFree += v_item.volume * v_item.endDT.size();
                this.volumeFree = Math.min(this.volumeFree, Math.round(v_item.volume * Constants.itemVolumeFreeCoefficient));
                return v_item;
            }
        }
        return null;
    }


    //Разделение стаков для дробления и передачи
    ItemType takeItem (int p_quantity) {
        ItemType v_item = cloneEmpty(this);
        int v_quantity = p_quantity;
        int v_size = this.endDT.size();
        if (v_quantity > v_size) {v_quantity = v_size;}
        while (v_quantity > 0) {
            v_item.endDT.add(0, this.endDT.remove(this.endDT.size()-1));
            v_quantity--;
        }
        if (this.isContainer || this.isJar) {
            if (!this.contain.isEmpty()) {
                for (int i = 0; i < this.contain.size(); i++) {
                    v_item.contain.add(this.contain.remove(i));
                }
            }
        }
        this.endDT.trimToSize();
        v_item.endDT.trimToSize();
        return v_item;
    }

    //Затирание изношенных предметов
    //Перебирает все времена жизни пока не найдет просроченное, а тогда удаляет все объекты из стака
    //после найденного (включительно).
    void brokeItem (int[] p_dateTime) {
        for (int i = 0; i < endDT.size(); i++) {
            if ((endDT.get(i)[0] < p_dateTime[0])&&
                    (endDT.get(i)[1] < p_dateTime[1])&&
                    (endDT.get(i)[2] < p_dateTime[2])&&
                    (endDT.get(i)[3] < p_dateTime[3])&&
                    (endDT.get(i)[4] < p_dateTime[4])) {
                for (int j = endDT.size()-1; j >= i; j--) {
                    endDT.remove(j);
                }
                endDT.trimToSize();
                return;
            }
        }
    }
}
