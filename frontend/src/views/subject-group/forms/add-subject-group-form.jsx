/* eslint-disable react/prop-types */
/* eslint-disable no-unused-vars */
import React, { useEffect } from "react";
import { Form, Input, Modal } from "antd";

const { TextArea } = Input;

const AddSubjectGroupForm = ({ visible, onCancel, onOk, confirmLoading }) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible) {
      form.resetFields();
    }
  }, [visible, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      onOk(values);
    } catch (info) {
      console.log("Validate Failed:", info);
    }
  };

  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 8 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };

  return (
    <Modal
      title="Tambah Rumpun Mata Kuliah"
      open={visible}
      onCancel={() => {
        form.resetFields();
        onCancel();
      }}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
    >
      <Form form={form} {...formItemLayout}>
        <Form.Item
          label="Nama:"
          name="name"
          rules={[{ required: true, message: "Silahkan isikan nama rumpun mata kuliah" }]}
        >
          <Input placeholder="Nama Rumpun Mata Kuliah" />
        </Form.Item>

        <Form.Item
          label="Deskripsi:"
          name="description"
          rules={[{ required: true, message: "Silahkan isikan deskripsi rumpun mata kuliah" }]}
        >
          <TextArea rows={4} placeholder="Deskripsi Rumpun Mata Kuliah" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default AddSubjectGroupForm;
