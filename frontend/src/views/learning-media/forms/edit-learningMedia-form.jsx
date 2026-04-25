/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useEffect } from "react";
import { Form, Input, Modal, Select } from "antd";

const { TextArea } = Input;
const { Option } = Select;

const EditLearningMediaForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  currentRowData,
}) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible && currentRowData) {
      form.setFieldsValue({
        id: currentRowData.id,
        name: currentRowData.name,
        type: currentRowData.type,
        description: currentRowData.description,
      });
    }
  }, [visible, currentRowData, form]);

  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 8 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (info) {
      console.log("Validate Failed:", info);
    }
  };

  return (
    <Modal
      title="Edit Media Pembelajaran"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleOk}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
    >
      <Form {...formItemLayout} form={form}>
        <Form.Item label="ID Media:" name="id">
          <Input disabled />
        </Form.Item>

        <Form.Item
          label="Nama Media:"
          name="name"
          rules={[{ required: true, message: "Silahkan isikan nama media pembelajaran" }]}
        >
          <Input placeholder="Nama Media Pembelajaran" />
        </Form.Item>

        <Form.Item
          label="Tipe Media:"
          name="type"
          rules={[{ required: true, message: "Silahkan pilih tipe media" }]}
        >
          <Select placeholder="Pilih Tipe Media">
            <Option value="software">Software</Option>
            <Option value="hardware">Hardware</Option>
            <Option value="hybrid">Hybrid</Option>
            <Option value="other">Lainnya</Option>
          </Select>
        </Form.Item>

        <Form.Item
          label="Deskripsi:"
          name="description"
          rules={[{ required: true, message: "Silahkan isikan deskripsi media pembelajaran" }]}
        >
          <TextArea rows={4} placeholder="Deskripsi Media" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default EditLearningMediaForm;
