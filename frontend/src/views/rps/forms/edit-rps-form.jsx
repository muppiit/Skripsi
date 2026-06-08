/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useEffect } from "react";
import { Form, Input, Select, Modal, InputNumber } from "antd";

const EditRPSForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  currentRowData,
  studyPrograms = [],
  learningMediaSoftwares = [],
  learningMediaHardwares = [],
  subjects = [],
  lectures = [],
  semesterList = [],
}) => {
  const [form] = Form.useForm();

  const renderSemesterLabel = (semester) => {
    const studyProgramName =
      semester?.studyProgram?.nameSchool ||
      semester?.study_program?.nameSchool ||
      semester?.studyProgram?.name ||
      semester?.study_program?.name ||
      semester?.studyProgram?.nama ||
      semester?.study_program?.nama ||
      "Tanpa Program Studi";

    return `${semester.namaSemester} (${studyProgramName})`;
  };

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

  useEffect(() => {
    if (!visible) {
      form.resetFields();
      return;
    }

    const subjectId = getRelationId(currentRowData.subject, subjects);
    const requirementSubjectIds = getRelationIds(
      currentRowData.requirement_subjects,
      subjects
    );

    form.setFieldsValue({
      id: currentRowData.id,
      name: currentRowData.name,
      sks: currentRowData.sks,
      semester:
        currentRowData.semester !== undefined && currentRowData.semester !== null
          ? String(currentRowData.semester)
          : undefined,
      cpl_prodi: currentRowData.cpl_prodi,
      cpl_mk: currentRowData.cpl_mk,
      learning_media_softwares:
        currentRowData.learning_media_softwares?.map((software) => software.id) ||
        [],
      learning_media_hardwares:
        currentRowData.learning_media_hardwares?.map((hardware) => hardware.id) ||
        [],
      study_program_id: currentRowData.study_program?.id,
      subject_id: subjectId,
      requirement_subjects:
        requirementSubjectIds.length > 0
          ? requirementSubjectIds
          : subjectId
            ? [subjectId]
            : [],
      dev_lecturers: getRelationIds(currentRowData.dev_lecturers, lectures),
      teaching_lecturers: getRelationIds(
        currentRowData.teaching_lecturers,
        lectures
      ),
      coordinator_lecturers: getRelationIds(
        currentRowData.coordinator_lecturers,
        lectures
      ),
      ka_study_program: getRelationId(currentRowData.ka_study_program, lectures),
    });
  }, [currentRowData, form, lectures, subjects, visible]);

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
      title="mengedit"
      visible={visible}
      onCancel={onCancel}
      onOk={() => {
        form
          .validateFields()
          .then((values) => {
            onOk(values);
          })
          .catch((info) => {
            console.log("Validate Failed:", info);
          });
      }}
      confirmLoading={confirmLoading}
    >
      <Form
        {...formItemLayout}
        form={form}
      >
        <Form.Item label="ID Pengguna:" name="id">
          <Input disabled />
        </Form.Item>

        <Form.Item
          label="Nama:"
          name="name"
          rules={[{ required: true, message: "Nama wajib diisi" }]}
        >
          <Input placeholder="Nama" />
        </Form.Item>

        <Form.Item
          label="SKS:"
          name="sks"
          rules={[{ required: true, message: "SKS wajib diisi" }]}
        >
          <InputNumber placeholder="SKS RPS" min={1} style={{ width: 300 }} />
        </Form.Item>

        <Form.Item
          label="Semester:"
          name="semester"
          rules={[{ required: true, message: "Semester wajib diisi" }]}
        >
          <Select style={{ width: 300 }} placeholder="Pilih Semester">
            {semesterList.map((item, key) => (
              <Select.Option
                value={item.idSemester}
                key={`semester-${key}`}
              >
                {renderSemesterLabel(item)}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="CPL Prodi:"
          name="cpl_prodi"
          rules={[{ required: true, message: "CPL Prodi wajib diisi" }]}
        >
          <Input placeholder="CPL Prodi" />
        </Form.Item>

        <Form.Item
          label="CPL Mata Kuliah:"
          name="cpl_mk"
          rules={[{ required: true, message: "CPL Mata Kuliah wajib diisi" }]}
        >
          <Input placeholder="CPL Mata Kuliah" />
        </Form.Item>

        <Form.Item
          label="Learning Media Softwares"
          name="learning_media_softwares"
          rules={[
            { required: true, message: "Learning Media Softwares is required" },
          ]}
        >
          <Select
            mode="multiple"
            style={{ width: 300 }}
            placeholder="Select Learning Media Softwares"
          >
            {learningMediaSoftwares?.map((software, key) => (
              <Select.Option value={software.id} key={`software-${key}`}>
                {software.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Hardware Media Pembelajaran:"
          name="learning_media_hardwares"
          rules={[
            {
              required: true,
              message: "Silahkan pilih Hardware Media Pembelajaran",
            },
          ]}
        >
          <Select
            mode="multiple"
            style={{ width: 300 }}
            placeholder="Pilih Hardware Media Pembelajaran"
          >
            {learningMediaHardwares?.map((hardware, key) => (
              <Select.Option value={hardware.id} key={`hardware-${key}`}>
                {hardware.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Subject ID"
          name="subject_id"
          rules={[{ required: true, message: "Subject ID is required" }]}
        >
          <Select style={{ width: 300 }} placeholder="Subject ID">
            {subjects?.map((subject, key) => (
              <Select.Option value={subject.id} key={`subject-${key}`}>
                {subject.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Mata Kuliah Wajib:"
          name="requirement_subjects"
          rules={[{ required: true, message: "Silahkan pilih matkul wajib" }]}
        >
          <Select
            mode="multiple"
            style={{ width: 300 }}
            placeholder="Pilih Matkul Wajib"
          >
            {subjects?.map((subject, key) => (
              <Select.Option value={subject.id} key={`requirement-subject-${key}`}>
                {subject.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Dosen Pengembang:"
          name="dev_lecturers"
          rules={[
            { required: true, message: "Silahkan pilih dosen pengembang" },
          ]}
        >
          <Select
            mode="multiple"
            style={{ width: 300 }}
            placeholder="Pilih Dosen Pengembang"
          >
            {lectures?.map((arr, key) => (
              <Select.Option value={arr.id} key={`dev-lecture-${key}`}>
                {arr.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Dosen Pengampu:"
          name="teaching_lecturers"
          rules={[{ required: true, message: "Silahkan pilih dosen pengampu" }]}
        >
          <Select
            mode="multiple"
            style={{ width: 300 }}
            placeholder="Pilih Dosen Pengampu"
          >
            {lectures?.map((arr, key) => (
              <Select.Option value={arr.id} key={`teaching-lecture-${key}`}>
                {arr.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Dosen Koordinator:"
          name="coordinator_lecturers"
          rules={[
            { required: true, message: "Silahkan pilih dosen koordinator" },
          ]}
        >
          <Select
            mode="multiple"
            style={{ width: 300 }}
            placeholder="Pilih Dosen Koordinator"
          >
            {lectures?.map((arr, key) => (
              <Select.Option value={arr.id} key={`coordinator-lecture-${key}`}>
                {arr.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Ka Prodi:"
          name="ka_study_program"
          rules={[{ required: true, message: "Silahkan pilih Ka Prodi" }]}
        >
          <Select style={{ width: 300 }} placeholder="Pilih Ka Prodi">
            {lectures?.map((arr, key) => (
              <Select.Option value={arr.id} key={`ka-prodi-${key}`}>
                {arr.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Program Study (Prodi):"
          name="study_program_id"
          rules={[{ required: true, message: "Silahkan pilih prodi" }]}
        >
          <Select style={{ width: 300 }} placeholder="Pilih Prodi">
            {studyPrograms?.map((arr, key) => (
              <Select.Option value={arr.id} key={`study-program-${key}`}>
                {arr.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default EditRPSForm;
