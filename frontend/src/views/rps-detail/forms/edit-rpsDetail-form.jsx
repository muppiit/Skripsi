/* eslint-disable react/prop-types */
import React, { useEffect } from "react";
import { Form, Input, InputNumber, Modal, Select } from "antd";

const { TextArea } = Input;

const EditRPSDetailForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  currentRowData = {},
  formLearnings = [],
  learningMethods = [],
  assessmentCriterias = [],
  appraisalForms = [],
}) => {
  const [form] = Form.useForm();

  const getRelationId = (relation, source = []) => {
    if (!relation) {
      return undefined;
    }

    if (typeof relation === "string") {
      return relation;
    }

    if (relation.id) {
      return relation.id;
    }

    if (relation.name) {
      return source.find((item) => item.name === relation.name)?.id;
    }

    return undefined;
  };

  const getRelationIds = (relations = [], source = []) =>
    relations
      .map((relation) => getRelationId(relation, source))
      .filter(Boolean);

  const normalizeListField = (value) => {
    if (Array.isArray(value)) {
      return value;
    }

    if (typeof value === "string") {
      return value
        .split(/\n|,/)
        .map((item) => item.trim())
        .filter(Boolean);
    }

    return [];
  };

  const joinListField = (value) =>
    Array.isArray(value) ? value.join("\n") : value || "";

  useEffect(() => {
    if (!visible) {
      form.resetFields();
      return;
    }

    form.setFieldsValue({
      id: currentRowData.id,
      week: currentRowData.week,
      sub_cp_mk: currentRowData.sub_cp_mk,
      learning_materials: joinListField(currentRowData.learning_materials),
      form_learning_id: getRelationId(
        currentRowData.form_learning,
        formLearnings
      ),
      learning_methods: getRelationIds(
        currentRowData.learning_methods,
        learningMethods
      ),
      assignments: joinListField(currentRowData.assignments),
      estimated_times: joinListField(currentRowData.estimated_times),
      student_learning_experiences: joinListField(
        currentRowData.student_learning_experiences
      ),
      assessment_criterias: getRelationIds(
        currentRowData.assessment_criterias,
        assessmentCriterias
      ),
      appraisal_forms: getRelationIds(
        currentRowData.appraisal_forms,
        appraisalForms
      ),
      assessment_indicators: joinListField(currentRowData.assessment_indicators),
      weight: currentRowData.weight,
    });
  }, [
    appraisalForms,
    assessmentCriterias,
    currentRowData,
    form,
    formLearnings,
    learningMethods,
    visible,
  ]);

  const formItemLayout = {
    labelCol: {
      xs: { span: 24 },
      sm: { span: 8 },
    },
    wrapperCol: {
      xs: { span: 24 },
      sm: { span: 16 },
    },
  };

  return (
    <Modal
      width={1000}
      title="Edit Detail RPS"
      visible={visible}
      onCancel={onCancel}
      onOk={() => {
        form
          .validateFields()
          .then((values) =>
            onOk({
              ...values,
              learning_materials: normalizeListField(values.learning_materials),
              assignments: normalizeListField(values.assignments),
              estimated_times: normalizeListField(values.estimated_times),
              student_learning_experiences: normalizeListField(
                values.student_learning_experiences
              ),
              assessment_indicators: normalizeListField(
                values.assessment_indicators
              ),
            })
          )
          .catch((info) => {
            console.log("Validate Failed:", info);
          });
      }}
      confirmLoading={confirmLoading}
      forceRender
    >
      <Form form={form} {...formItemLayout}>
        <Form.Item label="ID Detail RPS:" name="id">
          <Input disabled />
        </Form.Item>

        <Form.Item
          label="Minggu Ke:"
          name="week"
          rules={[{ required: true, message: "Minggu wajib diisi" }]}
        >
          <InputNumber placeholder="Minggu ke" min={1} style={{ width: 300 }} />
        </Form.Item>

        <Form.Item
          label="Sub CP MK:"
          name="sub_cp_mk"
          rules={[{ required: true, message: "Sub CP MK wajib diisi" }]}
        >
          <TextArea placeholder="Sub CP MK" style={{ width: 300 }} />
        </Form.Item>

        <Form.Item
          label="Materi Pembelajaran:"
          name="learning_materials"
          rules={[
            { required: true, message: "Silahkan isikan materi pembelajaran" },
          ]}
        >
          <TextArea
            rows={3}
            placeholder="Isikan Materi Pembelajaran, pisahkan dengan baris baru atau koma"
            style={{ width: 300 }}
          />
        </Form.Item>

        <Form.Item
          label="Bentuk Pembelajaran:"
          name="form_learning_id"
          rules={[
            { required: true, message: "Silahkan pilih bentuk pembelajaran" },
          ]}
        >
          <Select style={{ width: 300 }} placeholder="Pilih Bentuk Pembelajaran">
            {formLearnings.map((item) => (
              <Select.Option value={item.id} key={item.id}>
                {item.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Metode Pembelajaran:"
          name="learning_methods"
          rules={[
            { required: true, message: "Silahkan pilih metode pembelajaran" },
          ]}
        >
          <Select
            mode="multiple"
            style={{ width: 300 }}
            placeholder="Pilih Metode Pembelajaran"
          >
            {learningMethods.map((item) => (
              <Select.Option value={item.id} key={item.id}>
                {item.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Penugasan:"
          name="assignments"
          rules={[{ required: true, message: "Silahkan isikan penugasan" }]}
        >
          <TextArea
            rows={3}
            placeholder="Isikan Penugasan, pisahkan dengan baris baru atau koma"
            style={{ width: 300 }}
          />
        </Form.Item>

        <Form.Item
          label="Estimasi Waktu:"
          name="estimated_times"
          rules={[{ required: true, message: "Silahkan isi estimasi waktu" }]}
        >
          <TextArea
            rows={3}
            placeholder="Isikan Estimasi Waktu, pisahkan dengan baris baru atau koma"
            style={{ width: 300 }}
          />
        </Form.Item>

        <Form.Item
          label="Pengalaman Belajar Mahasiswa:"
          name="student_learning_experiences"
          rules={[
            {
              required: true,
              message: "Silahkan isi pengalaman belajar mahasiswa",
            },
          ]}
        >
          <TextArea
            rows={3}
            placeholder="Isikan Pengalaman Belajar Mahasiswa, pisahkan dengan baris baru atau koma"
            style={{ width: 300 }}
          />
        </Form.Item>

        <Form.Item
          label="Kriteria Penilaian:"
          name="assessment_criterias"
          rules={[
            { required: true, message: "Silahkan pilih kriteria penilaian" },
          ]}
        >
          <Select
            mode="multiple"
            style={{ width: 300 }}
            placeholder="Pilih Kriteria Penilaian"
          >
            {assessmentCriterias.map((item) => (
              <Select.Option value={item.id} key={item.id}>
                {item.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Bentuk Penilaian:"
          name="appraisal_forms"
          rules={[
            { required: true, message: "Silahkan pilih bentuk penilaian" },
          ]}
        >
          <Select
            mode="multiple"
            style={{ width: 300 }}
            placeholder="Pilih Bentuk Penilaian"
          >
            {appraisalForms.map((item) => (
              <Select.Option value={item.id} key={item.id}>
                {item.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Indikator Penilaian:"
          name="assessment_indicators"
          rules={[
            { required: true, message: "Silahkan isi indikator penilaian" },
          ]}
        >
          <TextArea
            rows={3}
            placeholder="Isikan Indikator Penilaian, pisahkan dengan baris baru atau koma"
            style={{ width: 300 }}
          />
        </Form.Item>

        <Form.Item
          label="Bobot:"
          name="weight"
          rules={[{ required: true, message: "Bobot wajib diisi" }]}
        >
          <InputNumber placeholder="Bobot" min={1} style={{ width: 300 }} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default EditRPSDetailForm;
