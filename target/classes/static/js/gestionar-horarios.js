$(document).ready(function () {
  const medicoId = $("#medicoId").val();
  const $tabla = $("#tabla-horarios tbody");
  const $dia = $("#dia");
  const $horaInicio = $("#horaInicio");
  const $horaFin = $("#horaFin");
  const $form = $("#form-horario");

  function cargarHorarios() {
    $tabla.empty();
    $.get(`/api/horario/${medicoId}`, function (horarios) {
      console.log("Horarios cargados:", horarios);
      if (horarios.length === 0) {
        $tabla.append('<tr><td colspan="4" class="text-center">Sin horarios registrados</td></tr>');
        return;
      }
      horarios.forEach(h => {
        const row = `<tr>
          <td>${h.diaSemana}</td>
          <td>${h.horaInicio}</td>
          <td>${h.horaFin}</td>
          <td>
            <button class="btn btn-sm btn-danger" data-id="${h.id}"><i class="bi bi-trash"></i></button>
          </td>
        </tr>`;
        $tabla.append(row);
      });
    });
  }

  $tabla.on("click", ".btn-danger", function () {
    const id = $(this).data("id");
    if (confirm("¿Seguro que deseas eliminar este horario?")) {
      $.ajax({
        url: `/api/horario/${id}`,
        type: "DELETE",
        success: cargarHorarios,
        error: () => alert("Error al eliminar horario")
      });
    }
  });

  $form.on("submit", function (e) {
    e.preventDefault();
    const nuevo = {
      medicoId: medicoId ,
      diaSemana: $dia.val(),
      horaInicio: $horaInicio.val(),
      horaFin: $horaFin.val(),
    };

    $.ajax({
      url: "/api/horario",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(nuevo),
      success: function () {
        $form.trigger("reset");
        cargarHorarios();
      },
      error: function (xhr) {
        alert(xhr.responseText || "Error al guardar horario");
      }
    });
  });

  cargarHorarios();
});
