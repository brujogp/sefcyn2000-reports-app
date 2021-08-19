package com.sefcyn2000.reports.ui.fragments.templates.newtemplates;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.data.entities.Template;
import com.sefcyn2000.reports.data.entities.templatecomponents.Category;
import com.sefcyn2000.reports.data.entities.templatecomponents.Section;
import com.sefcyn2000.reports.data.entities.templatecomponents.Zone;
import com.sefcyn2000.reports.ui.activities.templates.NewTemplateActivity;
import com.sefcyn2000.reports.utilities.constans.TagTypeItemTemplateEnum;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DefinitionFormMapFragment extends Fragment implements View.OnClickListener, NewTemplateActivity.VerifySecondStepForm {
    LinearLayout linearParentCategoriesContainer;

    private boolean isFormValid;

    private Template template;
    private boolean onBuildingForm = false;

    public DefinitionFormMapFragment(Template template) {
        this.template = template;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_template_definition_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.linearParentCategoriesContainer = view.findViewById(R.id.linear_layout_categories_container);

        if (this.template.getCategories() == null || this.template.getCategories().size() == 0) {

            LinearLayout firstFieldCategory = newCategory("");

            this.linearParentCategoriesContainer.addView(firstFieldCategory);

            firstFieldCategory.addView(newZone(""));
        } else {
            regenerateForm();
        }
    }

    LinearLayout newCategory(@NotNull String categoryTextName) {
        LinearLayout linearLayoutContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_template_item_category_section, null);
        linearLayoutContainer.setTag(TagTypeItemTemplateEnum.CONTAINER_CATEGORY.getTag());

        // Recupero el EditText que está dentro del contenedor junto con el botón
        TextInputEditText textInputEditText = linearLayoutContainer.findViewById(R.id.et_item);

        // Configuro el botón para eliminiar elementos
        ImageButton imageButton = linearLayoutContainer.findViewById(R.id.btn_delete_item);
        imageButton.setTag("btn_delete_category");
        imageButton.setOnClickListener(this);

        textInputEditText.setText(categoryTextName);
        final boolean[] wasAppend = {false};


        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!onBuildingForm) {
                    if (linearParentCategoriesContainer.getChildCount() == 1) {
                        addNewItem(textInputEditText, linearParentCategoriesContainer);


                    } else {
                        String penultimate = ((TextInputEditText) linearParentCategoriesContainer.getChildAt(linearParentCategoriesContainer.getChildCount() - 2).findViewById(R.id.et_item)).getText().toString();
                        String latest = ((TextInputEditText) linearParentCategoriesContainer.getChildAt(linearParentCategoriesContainer.getChildCount() - 1).findViewById(R.id.et_item)).getText().toString();

                        if (!(latest.isEmpty()) && !(penultimate.isEmpty())) {
                            addNewItem(textInputEditText, linearParentCategoriesContainer);
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!wasAppend[0] && editable.length() == 0) {
                    wasAppend[0] = true;
                    editable.append(categoryTextName);
                }else{
                    wasAppend[0] = true;
                }
            }
        });

        textInputEditText.setTag("category");
        ((TextInputLayout) textInputEditText.getParent().getParent()).setHint("Nombre de la categoría");

        return linearLayoutContainer;
    }

    LinearLayout newZone(@NotNull String zoneTextName) {
        LinearLayout linearLayoutContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_template_item_zone, null);
        linearLayoutContainer.setTag(TagTypeItemTemplateEnum.CONTAINER_ZONE.getTag());

        TextInputEditText textInputEditText = linearLayoutContainer.findViewById(R.id.et_zone);

        ImageButton imageButton = linearLayoutContainer.findViewById(R.id.btn_delete_zone);
        imageButton.setTag("btn_delete_zone");
        imageButton.setOnClickListener(this);

        textInputEditText.setText(zoneTextName);
        final boolean[] wasAppend = {false};

        textInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!wasAppend[0] && editable.length() == 0) {
                    wasAppend[0] = true;
                    editable.append(zoneTextName);
                }else{
                    wasAppend[0] = true;
                }

                if (!onBuildingForm) {
                    TextInputEditText categoryEditText = ((View) linearLayoutContainer.getParent()).findViewById(R.id.et_item);

                    if (categoryEditText.getText().toString().isEmpty() && !editable.toString().isEmpty()) {
                        isFormValid = false;

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder
                                .setTitle("Categoría sin nombre")
                                .setMessage("Primero asigne un nombre a la categoría para crear nuevas zonas")
                                .setPositiveButton("Está bien", (dialog, which) -> {
                                    textInputEditText.setText(null);
                                    dialog.dismiss();
                                })
                                .create()
                                .show();

                    } else if (!editable.toString().isEmpty()) {
                        LinearLayout parentZoneLinearLayout = ((LinearLayout) linearLayoutContainer.getParent());

                        if (parentZoneLinearLayout.getChildCount() == 2) {
                            addNewItem(textInputEditText, linearLayoutContainer);
                        } else {
                            String penultimate = ((TextInputEditText) parentZoneLinearLayout.getChildAt(parentZoneLinearLayout.getChildCount() - 2).findViewById(R.id.et_zone)).getText().toString();
                            String latest = ((TextInputEditText) parentZoneLinearLayout.getChildAt(parentZoneLinearLayout.getChildCount() - 1).findViewById(R.id.et_zone)).getText().toString();

                            if (!(latest.isEmpty()) && !(penultimate.isEmpty())) {
                                addNewItem(textInputEditText, linearLayoutContainer);
                            }
                        }
                    }
                }
            }
        });

        textInputEditText.setTag("zone");
        ((TextInputLayout) textInputEditText.getParent().getParent()).setHint("Nombre de la zona");

        // Configuro el botón para añadir secciones a la zona
        linearLayoutContainer.findViewById(R.id.btn_new_section).setOnClickListener(view -> {
            LinearLayout linearLayoutP = (LinearLayout) view.getParent().getParent().getParent();
            linearLayoutP.addView(newSection(""));
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(7, 7, 0, 0);
        linearLayoutContainer.setLayoutParams(layoutParams);


        return linearLayoutContainer;
    }

    LinearLayout newSection(@NotNull String sectionTextName) {
        LinearLayout linearLayoutContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_template_item_category_section, null);
        linearLayoutContainer.setTag(TagTypeItemTemplateEnum.CONTAINER_SECTION.getTag());

        TextInputEditText textInputEditText = linearLayoutContainer.findViewById(R.id.et_item);

        ImageButton imageButton = linearLayoutContainer.findViewById(R.id.btn_delete_item);
        imageButton.setTag("btn_delete_section");
        imageButton.setOnClickListener(this);

        textInputEditText.setTag("section");
        ((TextInputLayout) textInputEditText.getParent().getParent()).setHint("Nombre de la sección");

        textInputEditText.setText(sectionTextName);
        final boolean[] wasAppend = {false};

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!wasAppend[0] && s.length() == 0) {
                    wasAppend[0] = true;
                    s.append(sectionTextName);
                }else{
                    wasAppend[0] = true;
                }

                if (!onBuildingForm) {
                    TextInputEditText inputEditTextSection = ((View) linearLayoutContainer.getParent()).findViewById(R.id.et_zone);

                    if (inputEditTextSection.getText().toString().isEmpty() && !s.toString().isEmpty()) {
                        isFormValid = false;

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder
                                .setTitle("Zona sin nombre")
                                .setMessage("Primero asigne un nombre a la zona para crear nuevas secciones")
                                .setPositiveButton("Está bien", (dialog, which) -> {
                                    textInputEditText.setText(null);
                                    dialog.dismiss();
                                })
                                .create()
                                .show();
                    }
                }
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(270, 7, 0, 0);
        linearLayoutContainer.setLayoutParams(layoutParams);

        return linearLayoutContainer;
    }


    void addNewItem(View view, LinearLayout parentContainer) {
        if (view.getTag().toString().equals("category")) {

            LinearLayout linearCategory = newCategory("");
            parentContainer.addView(linearCategory);

            linearCategory.addView(newZone(""));

        } else if (view.getTag().toString().equals("zone")) {

            ((LinearLayout) parentContainer.getParent()).addView(newZone(""));

        } else if (view.getTag().toString().equals("section")) {

            ((LinearLayout) parentContainer.getParent()).addView(newSection(""));

        }
    }


    private boolean verifyToSave() {
        boolean isFormValid = true;

        View parentContainer = getView().findViewById(R.id.linear_layout_categories_container);

        int categoriesCounter = ((ViewGroup) parentContainer).getChildCount();

        for (int i = 0; categoriesCounter > i; i++) {
            TextInputEditText textInputEditText = ((ViewGroup) parentContainer).getChildAt(i).findViewById(R.id.et_item);

            // Si el formulario solamente tiene una categoría sin rellenar
            if (categoriesCounter == 1 && textInputEditText.getText().toString().isEmpty()) {
                isFormValid = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Debe agregar por lo menos una categoría con alguna zona")
                        .setPositiveButton("Está bien", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            } else if (textInputEditText.getText().toString().isEmpty()) {
                TextInputEditText edZone = ((ViewGroup) parentContainer).getChildAt(i).findViewById(R.id.et_zone);

                if (!edZone.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder
                            .setTitle("Hay una o más categorías sin nombre")
                            .setMessage("Se detectó una o más categorías sin nombre y con zonas ya definidas. Dele un nombre a la o las categorias y vuelva a intentar.")
                            .setPositiveButton("Está bien", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .create()
                            .show();

                    return false;
                }
            } else if (!textInputEditText.getText().toString().isEmpty()) {
                LinearLayout linearLayout = ((ViewGroup) parentContainer).getChildAt(i).findViewById(R.id.linear_layout_container_item);

                if (linearLayout.getChildCount() >= 2) {

                    TextInputEditText etZone = ((ViewGroup) parentContainer).getChildAt(i).findViewById(R.id.et_zone);

                    if (etZone.getText().toString().isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Hay una o más categorías vacías")
                                .setMessage("Todas las categorías deben de tener al menos una zona. Si tiene muchos campos para zonas vacíos, elimínelos.")
                                .setPositiveButton("Está bien", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .create()
                                .show();

                        return false;
                    }
                }
            }

        }

        return isFormValid;
    }


    private void regenerateForm() {
        this.onBuildingForm = true;

        List<Category> categoryList = this.template.getCategories();

        for (Category categoryFromList : categoryList) {

            LinearLayout categoryLinearLayout = newCategory(categoryFromList.getNameCategory());

            this.linearParentCategoriesContainer.addView(categoryLinearLayout);

            for (Zone zoneFromList : categoryFromList.getZones()) {

                LinearLayout zoneLinearLayout = newZone(zoneFromList.getNameZone());

                categoryLinearLayout.addView(zoneLinearLayout);

                if (zoneFromList.getSections() != null && zoneFromList.getSections().size() > 0) {

                    for (Section sectionFromList : zoneFromList.getSections()) {

                        LinearLayout sectionLinearLayout = newSection(sectionFromList.getNameSection());

                        zoneLinearLayout.addView(sectionLinearLayout);

                    }

                }

            }
        }

        this.onBuildingForm = false;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag() == "btn_delete_zone") {
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();

            if (linearLayout.getChildCount() == 2) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder
                        .setMessage("Siempre se conservará por lo menos un campo para la zona")
                        .setPositiveButton("Está bien", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create()
                        .show();
            } else {
                deleteView(view);
            }

        } else if (view.getTag().equals("btn_delete_category")) {
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent();

            if (linearLayout.getChildCount() == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder
                        .setMessage("Siempre se conservará por lo menos un campo para la categoría")
                        .setPositiveButton("Está bien", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .create()
                        .show();
            } else {
                deleteView(view);
            }
        } else {
            deleteView(view);
        }

    }

    private void deleteView(View view) {
        ImageButton imageButton = (ImageButton) view;
        LinearLayout linearLayoutParent = (LinearLayout) imageButton.getParent().getParent().getParent();
        LinearLayout linearLayoutToRemove = (LinearLayout) imageButton.getParent().getParent();

        linearLayoutParent.removeView(linearLayoutToRemove);
    }

    @Override
    public boolean isValidSecondFormStep() {
        return verifyToSave();
    }

    @Override
    public boolean getInfoFromDefinitionTemplateForm() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Espere");
        progressDialog.create();
        progressDialog.show();

        LinearLayout categoriesContainer = getView().findViewById(R.id.linear_layout_categories_container);
        boolean isValidForm = true;

        int categoriesWithText = 0;

        this.template.setCategories(new ArrayList<>());

        // Recorro todos los campos para categorías
        for (int indexCategory = 0; categoriesContainer.getChildCount() > indexCategory; indexCategory++) {

            // Obtengo un elemento Categoría
            LinearLayout category = (LinearLayout) categoriesContainer.getChildAt(indexCategory);
            TextInputEditText etCategory = category.findViewById(R.id.et_item);

            // Si la categoría tiene algo escrito
            if (!(etCategory.getText().toString().equals(""))) {

                categoriesWithText++;

                // Creo un objeto categoría y lo añado al listado de categorías de la plantilla
                Category catTemplate = new Category(etCategory.getText().toString());
                this.template.addCategory(catTemplate);

                // Recorro los hijos del contenedor de la categoria actual
                for (int indexZone = 1; category.getChildCount() > indexZone; indexZone++) {

                    // Obtengo los campos para las zonas desde el contenedor de la cateogria actual
                    LinearLayout zone = (LinearLayout) category.getChildAt(indexZone);
                    TextInputEditText etZone = zone.findViewById(R.id.et_zone);

                    // Si el campo para la zona tiene algo escrito
                    if (!(etZone.getText().toString().isEmpty())) {

                        // Creo un objeto zona y lo añado al listado de zonas del objeto categoria antes creado
                        Zone zoneTemplate = new Zone(etZone.getText().toString());
                        catTemplate.addZone(zoneTemplate);


                        // Recorro el listado de secciones
                        for (int indexSection = 1; zone.getChildCount() > indexSection; indexSection++) {

                            // Obtengo los tampos para la zonas
                            LinearLayout section = (LinearLayout) zone.getChildAt(indexSection);
                            TextInputEditText etSection = section.findViewById(R.id.et_item);

                            // Si el campo para la seccion actual tiene algo escrito
                            if (!(etSection.getText().toString().isEmpty())) {

                                // Creo un objeto seccion y lo añado al listad de secciones de la zona actual
                                Section sectionTemplate = new Section(etSection.getText().toString());
                                zoneTemplate.addSection(sectionTemplate);
                            }
                        }
                    }
                }
            }
        }

        if (categoriesWithText == 0) {
            isValidForm = false;
        }

        progressDialog.dismiss();

        return isValidForm;
    }
}
