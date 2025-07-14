$(document).ready(function () {
  const $especialidad = $("#especialidad");
  const $medico = $("#medico");
  const $fecha = $("#fecha");
  const $hora = $("#hora");
  const $btnAgendar = $("#btn-agendar");
  const pacienteId = $('#pacienteId').val();

  let flatpickrInstance = null;

  $medico.prop("disabled", true);
  $fecha.prop("disabled", true);
  $hora.prop("disabled", true);
  $btnAgendar.prop("disabled", true);

  function verificarFormulario() {
    const especialidadValida = $especialidad.val();
    const medicoValido = $medico.val();
    const fechaValida = $fecha.val();
    const horaValida = $hora.val();

    $btnAgendar.prop("disabled", !(especialidadValida && medicoValido && fechaValida && horaValida));
  }

  // Cargar especialidades
  $.get("/api/especialidades", function (data) {
    data.forEach(especialidad => {
      $especialidad.append(`<option value="${especialidad.id}">${especialidad.nombre}</option>`);
    });
  });

  // Cargar médicos por especialidad
  $especialidad.on("change", function () {
    const id = $(this).val();
    $medico.empty().append('<option selected disabled>Selecciona un médico</option>');
    $fecha.val("").prop("disabled", true);
    $hora.empty().append('<option selected disabled>Selecciona una hora</option>').prop("disabled", true);
    $btnAgendar.prop("disabled", true);

    $.get(`/api/medicos?especialidadId=${id}`, function (data) {
      data.forEach(medico => {
        const nombreCompleto = medico.nombre + " " + medico.apellido;
        $medico.append(`<option value="${medico.id}">${nombreCompleto}</option>`);
      });
      $medico.prop("disabled", false);
    });
  });

  // Configurar calendario con Flatpickr según días permitidos
  function configurarCalendario(diasPermitidos) {
    if (flatpickrInstance) {
      flatpickrInstance.destroy();
    }

    flatpickrInstance = flatpickr("#fecha", {
      dateFormat: "Y-m-d",
      minDate: "today",
      disable: [
        function (date) {
          const dias = ["sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"];
          const nombreDia = dias[date.getDay()];
          return !diasPermitidos.includes(nombreDia); // deshabilita si no está permitido
        }
      ],
      onChange: function () {
        $fecha.trigger("change");
      }
    });
  }

  // Al seleccionar médico
  $medico.on("change", function () {
    const medicoId = $(this).val();

    $.get(`/api/horarios/disponibles/${medicoId}`, function (diasPermitidos) {
      configurarCalendario(diasPermitidos);
      $fecha.prop("disabled", false);
      $hora.prop("disabled", true).empty().append('<option selected disabled>Selecciona una hora</option>');
      $btnAgendar.prop("disabled", true);
    });

    verificarFormulario();
  });

  // Al cambiar la fecha
  $fecha.on("change", function () {
    const medicoId = $medico.val();
    const fecha = $(this).val();

    $.get(`/api/horarios?medicoId=${medicoId}&fecha=${fecha}`, function (horas) {
      $hora.empty().append('<option selected disabled>Selecciona una hora</option>');
      horas.forEach(hora => {
        $hora.append(`<option value="${hora}">${hora}</option>`);
      });
      $hora.prop("disabled", false);
      verificarFormulario();
    });
  });

  // Escuchar cambios
  $especialidad.on("change", verificarFormulario);
  $medico.on("change", verificarFormulario);
  $fecha.on("change", verificarFormulario);
  $hora.on("change", verificarFormulario);

  // Enviar formulario
  $("form").on("submit", function (e) {
    e.preventDefault();

    const cita = {
      paciente: { id: pacienteId },
      medico: { id: $medico.val() },
      fecha: $fecha.val(),
      hora: $hora.val(),
      motivo: "Consulta general",
      especialidad: { id: $especialidad.val() },
    };

    $.ajax({
      type: "POST",
      url: "/api/citas",
      contentType: "application/json",
      data: JSON.stringify(cita),
      success: function () {
        window.location.href = "/paciente/citas/exito";
      },
      error: function () {
        alert("Error al agendar cita");
      }
    });
  });
});
