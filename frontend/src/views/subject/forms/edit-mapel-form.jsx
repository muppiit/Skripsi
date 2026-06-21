import React, { useEffect } from "react";
import { Col, Form, Input, InputNumber, Modal, Row, Select } from "antd";

const { TextArea } = Input;

const EditSubjectForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  currentRowData = {},
  studyPrograms = [],
  subjectGroups = [],
}) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible && currentRowData?.id) {
      form.setFieldsValue({
        id: currentRowData.id,
        name: currentRowData.name,
        description: currentRowData.description,
        credit_point: currentRowData.credit_point,
        year_commenced: currentRowData.year_commenced,
        study_program_id:
          currentRowData.studyProgram?.id || currentRowData.study_program?.id,
        subject_group_id: currentRowData.subject_group?.id,
      });
    }
  }, [currentRowData, form, visible]);

  const handleSubmit = async () => {
    const values = await form.validateFields();
    onOk(values);
  };

  return (
    <Modal
      title="Edit Mata Kuliah"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      width={720}
    >
      <Form form={form} layout="vertical">
        <Row gutter={16}>
          <Col xs={24} md={12}>
            <Form.Item label="ID Mata Kuliah" name="id">
              <Input disabled />
            </Form.Item>
          </Col>
          <Col xs={24} md={12}>
            <Form.Item
              label="Nama Mata Kuliah"
              name="name"
              rules={[{ required: true, message: "Silahkan isi nama mata kuliah" }]}
            >
              <Input placeholder="Contoh: Pemrograman Dasar" />
            </Form.Item>
          </Col>
          <Col xs={24} md={12}>
            <Form.Item
              label="Program Studi"
              name="study_program_id"
              rules={[{ required: true, message: "Silahkan pilih program studi" }]}
            >
              <Select placeholder="Pilih Program Studi" showSearch optionFilterProp="children">
                {studyPrograms.map((program) => (
                  <Select.Option key={program.id} value={program.id}>
                    {program.name}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} md={12}>
            <Form.Item
              label="Rumpun Mata Kuliah"
              name="subject_group_id"
              rules={[{ required: true, message: "Silahkan pilih rumpun mata kuliah" }]}
            >
              <Select placeholder="Pilih Rumpun Mata Kuliah" showSearch optionFilterProp="children">
                {subjectGroups.map((group) => (
                  <Select.Option key={group.id} value={group.id}>
                    {group.name}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} md={6}>
            <Form.Item
              label="SKS"
              name="credit_point"
              rules={[{ required: true, message: "Silahkan isi SKS" }]}
            >
              <InputNumber min={1} max={12} style={{ width: "100%" }} placeholder="SKS" />
            </Form.Item>
          </Col>
          <Col xs={24} md={6}>
            <Form.Item
              label="Tahun Mulai"
              name="year_commenced"
              rules={[{ required: true, message: "Silahkan isi tahun mulai" }]}
            >
              <InputNumber
                min={1900}
                max={2100}
                style={{ width: "100%" }}
                placeholder="Contoh: 2024"
              />
            </Form.Item>
          </Col>
          <Col xs={24}>
            <Form.Item
              label="Deskripsi"
              name="description"
              rules={[{ required: true, message: "Silahkan isi deskripsi mata kuliah" }]}
            >
              <TextArea rows={4} placeholder="Deskripsi singkat mata kuliah" />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
};

export default EditSubjectForm;
